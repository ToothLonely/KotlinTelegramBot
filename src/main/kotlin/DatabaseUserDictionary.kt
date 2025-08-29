package org.example

import java.sql.DriverManager

class DatabaseUserDictionary : IUserDictionary {

    private val connectionName = "jdbc:sqlite:data.db"

    override fun getNumOfLearnedWords(userName: String): Int {

        val getNumOfLearnedWordsQuery = """
            SELECT COUNT(word_id) AS count
            FROM user_answers
            WHERE correct_answers_count >= 3 AND user_name = '$userName'
        """.trimIndent()

        return DriverManager.getConnection(connectionName).use { connection ->
            connection.createStatement().executeQuery(getNumOfLearnedWordsQuery)
        }.getInt("count")
    }

    override fun getSize(): Int {
        val getSizeQuery = """
            SELECT MAX(id) AS max_id
            FROM words
        """.trimIndent()

        return DriverManager.getConnection(connectionName).use { connection ->
            connection.createStatement().executeQuery(getSizeQuery)
        }.getInt("max_id")
    }

    override fun getLearnedWords(userName: String): List<Word> {
        val getLearnedWordsQuery = """
            SELECT text, translate, correct_answers_count
            FROM user_answers AS ua 
            JOIN words AS w ON w.word_id = ua.word_id 
            WHERE correct_answers_count >= 3 AND user_name = '$userName'
        """.trimIndent()

        val learnedList = mutableListOf<Word>()

        DriverManager.getConnection(connectionName).use { connection ->
            val answer = connection.createStatement().executeQuery(getLearnedWordsQuery)
            while (answer.next()) {
                val word =
                    Word(
                        answer.getString("text"),
                        answer.getString("translate"),
                        answer.getInt("correct_answers_count")
                    )
                learnedList.add(word)
            }
        }

        return learnedList
    }

    override fun getUnlearnedWords(userName: String): List<Word> {
        val getLearnedWordsQuery = """
            SELECT text, translate, correct_answers_count
            FROM user_answers AS ua 
            JOIN words AS w ON w.word_id = ua.word_id 
            WHERE user_name = '$userName' AND correct_answers_count < 3
        """.trimIndent()

        val learnedList = mutableListOf<Word>()

        DriverManager.getConnection(connectionName).use { connection ->
            val answer = connection.createStatement().executeQuery(getLearnedWordsQuery)
            while (answer.next()) {
                val word =
                    Word(
                        answer.getString("text"),
                        answer.getString("translate"),
                        answer.getInt("correct_answers_count")
                    )
                learnedList.add(word)
            }
        }

        return learnedList
    }

    override fun setCorrectAnswersCount(word: String, correctAnswersCount: Int, userName: String) {
        val setCorrectAnswersCountQuery = """
            UPDATE user_answers
            SET correct_answers_count = $correctAnswersCount
            WHERE user_name = '$userName' AND word_id = (
                SELECT word_id 
                FROM words
                WHERE text = '$word'
            )
        """.trimIndent()

        DriverManager.getConnection(connectionName).use { connection ->
            connection.createStatement().executeUpdate(setCorrectAnswersCountQuery)
        }
    }

    override fun resetUserProgress(userName: String) {
        val resetUserProgressQuery = """
            UPDATE user_answers
            SET correct_answers_count = 0
            WHERE user_name = $userName
        """.trimIndent()

        DriverManager.getConnection(connectionName).use { connection ->
            connection.createStatement().executeUpdate(resetUserProgressQuery)
        }
    }

}