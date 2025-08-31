package org.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.PreparedStatement

class DatabaseUserDictionary : IUserDictionary {

    private val connectionName = "jdbc:sqlite:data.db"

    private val config = HikariConfig().apply {
        jdbcUrl = connectionName
    }

    private val dataSource = HikariDataSource(config)

    override fun getNumOfLearnedWords(userName: String): Int {

        val getNumOfLearnedWordsQuery = """
            SELECT COUNT(word_id) AS count
            FROM user_answers
            WHERE correct_answers_count >= 3 AND user_name = ?
        """.trimIndent()

        return dataSource.connection.use { connection ->
            val statement = connection.prepareStatement(getNumOfLearnedWordsQuery)
            statement.setString(1, userName)
            statement.executeQuery().getInt("count")
        }
    }

    override fun getSize(): Int {
        val getSizeQuery = """
            SELECT MAX(id) AS max_id
            FROM words
        """.trimIndent()

        return dataSource.connection.use { connection ->
            connection.createStatement().executeQuery(getSizeQuery).getInt("max_id")
        }
    }

    override fun getLearnedWords(userName: String): List<Word> {
        val getLearnedWordsQuery = """
            SELECT text, translate, correct_answers_count
            FROM user_answers AS ua 
            JOIN words AS w ON w.id = ua.word_id 
            WHERE correct_answers_count >= 3 AND user_name = ?
        """.trimIndent()

        val learnedList = mutableListOf<Word>()

        dataSource.connection.use { connection ->
            val statement = connection.prepareStatement(getLearnedWordsQuery)
            statement.setString(1, userName)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val word =
                    Word(
                        resultSet.getString("text"),
                        resultSet.getString("translate"),
                        resultSet.getInt("correct_answers_count")
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
            WHERE user_name = ? AND correct_answers_count < 3
        """.trimIndent()

        val unlearnedList = mutableListOf<Word>()

        dataSource.connection.use { connection ->
            val statement = connection.prepareStatement(getLearnedWordsQuery)
            statement.setString(1, userName)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val word =
                    Word(
                        resultSet.getString("text"),
                        resultSet.getString("translate"),
                        resultSet.getInt("correct_answers_count")
                    )
                unlearnedList.add(word)
            }
        }

        return unlearnedList
    }

    override fun setCorrectAnswersCount(word: String, correctAnswersCount: Int, userName: String) {
        val setCorrectAnswersCountQuery = """
            UPDATE user_answers
            SET correct_answers_count = ?
            WHERE user_name = ? AND word_id = (
                SELECT id 
                FROM words
                WHERE text = ?
                LIMIT 1
            )
        """.trimIndent()

        dataSource.connection.use { connection ->
            val statement: PreparedStatement = connection.prepareStatement(setCorrectAnswersCountQuery)
            with(statement) {
                setInt(1, correctAnswersCount)
                setString(2, userName)
                setString(3, word)
            }
            statement.executeUpdate()
        }
    }

    override fun resetUserProgress(userName: String) {
        val resetUserProgressQuery = """
            UPDATE user_answers
            SET correct_answers_count = 0
            WHERE user_name = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            val statement = connection.prepareStatement(resetUserProgressQuery)
            statement.setString(1, userName)
            statement.executeUpdate()
        }
    }

    override fun addUser(userName: String, chatId: Long) {

        val checkNewUserQuery = """
            SELECT COUNT(name) AS count_of_names
            FROM users
            WHERE name = ?
        """.trimIndent()

        val addUserInUsersQuery = """
            INSERT INTO users(name, chat_id)
            VALUES(?, ?)
        """.trimIndent()

        val addUserInUserAnswersQuery = """
            INSERT INTO user_answers(user_name, word_id)
            SELECT ?, id
            FROM words
        """.trimIndent()

        dataSource.connection.use { connection ->
            val checkingStatement = connection.prepareStatement(checkNewUserQuery)
            checkingStatement.setString(1, userName)
            val countOfUsers = checkingStatement.executeQuery().getInt("count_of_names")

            if (countOfUsers == 0) {
                val addInUsersStatement = connection.prepareStatement(addUserInUsersQuery)
                val addInUserAnswersStatement = connection.prepareStatement(addUserInUserAnswersQuery)

                with(addInUsersStatement){
                    setString(1, userName)
                    setLong(2, chatId)
                    executeUpdate()
                }

                with(addInUserAnswersStatement){
                    setString(1, userName)
                    executeUpdate()
                }
            }
        }
    }
}