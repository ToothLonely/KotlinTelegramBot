package org.example

import java.sql.DriverManager
import java.time.Instant
import java.time.format.DateTimeFormatter

class DatabaseUserDictionary : IUserDictionary {

    private val connectionName = "jdbc:sqlite:data.db"

    override fun getNumOfLearnedWords(userName: String): Int {

        val getNumOfLearnedWordsQuery = """
            SELECT COUNT(word_id) AS count
            FROM user_answers
            WHERE correct_answers_count >= 3 AND user_name = '$userName'
        """.trimIndent()

        return DriverManager.getConnection(connectionName).use { connection ->
            connection.createStatement().executeQuery(getNumOfLearnedWordsQuery).getInt("count")
        }
    }

    override fun getSize(): Int {
        val getSizeQuery = """
            SELECT MAX(id) AS max_id
            FROM words
        """.trimIndent()

        return DriverManager.getConnection(connectionName).use { connection ->
            connection.createStatement().executeQuery(getSizeQuery).getInt("max_id")
        }
    }

    override fun getLearnedWords(userName: String): List<Word> {
        val getLearnedWordsQuery = """
            SELECT text, translate, correct_answers_count
            FROM user_answers AS ua 
            JOIN words AS w ON w.id = ua.word_id 
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
            JOIN words AS w ON w.id = ua.word_id 
            WHERE user_name LIKE '$userName' AND correct_answers_count < 3
        """.trimIndent()

        val unlearnedList = mutableListOf<Word>()

        DriverManager.getConnection(connectionName).use { connection ->
            val answer = connection.createStatement().executeQuery(getLearnedWordsQuery)
            while (answer.next()) {
                val word =
                    Word(
                        answer.getString("text"),
                        answer.getString("translate"),
                        answer.getInt("correct_answers_count")
                    )
                unlearnedList.add(word)
            }
        }

        return unlearnedList
    }

    override fun setCorrectAnswersCount(word: String, correctAnswersCount: Int, userName: String) {
        val setCorrectAnswersCountQuery = """
            UPDATE user_answers
            SET correct_answers_count = $correctAnswersCount
            WHERE user_name = '$userName' AND word_id = (
                SELECT id 
                FROM words
                WHERE text = '$word'
                LIMIT 1
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
            WHERE user_name = '$userName'
        """.trimIndent()

        DriverManager.getConnection(connectionName).use { connection ->
            connection.createStatement().executeUpdate(resetUserProgressQuery)
        }
    }

    override fun addUser(userName: String, chatId: Long) {

        val checkNewUserQuery = """
            SELECT COUNT(name) AS count_of_names
            FROM users
            WHERE name = '$userName'
        """.trimIndent()

        val addUserInUsersQuery = """
            INSERT INTO users(name, chat_id)
            VALUES('$userName', $chatId)
        """.trimIndent()

        val addUserInUserAnswersQuery = """
            INSERT INTO user_answers(user_name, word_id)
            SELECT '$userName', id
            FROM words
        """.trimIndent()

        DriverManager.getConnection(connectionName).use { connection ->
            val statement = connection.createStatement()
            val countOfUsers = statement.executeQuery(checkNewUserQuery).getInt("count_of_names")
            if (countOfUsers == 0) {
                statement.executeUpdate(addUserInUsersQuery)
                statement.executeUpdate(addUserInUserAnswersQuery)
            }
        }
    }

}