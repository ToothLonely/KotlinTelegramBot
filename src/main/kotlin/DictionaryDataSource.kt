package org.example

import java.io.File
import java.sql.DriverManager
import java.sql.Statement

fun main() {
    DriverManager.getConnection("jdbc:sqlite:data.db").use { connection ->
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_WORDS.trimIndent())
        statement.executeUpdate(CREATE_TABLE_USER_ANSWERS.trimIndent())
        statement.executeUpdate(CREATE_TABLE_USERS.trimIndent())
        statement.executeUpdate("PRAGMA foreign_keys = ON;")
        updateDictionary(File("words.txt"), statement)
    }
}


const val CREATE_TABLE_WORDS = """
    CREATE TABLE IF NOT EXISTS 'words' (
    'id' integer PRIMARY KEY,
    'text' varchar,
    'translate' varchar
    ); 
"""

const val CREATE_TABLE_USER_ANSWERS = """
    CREATE TABLE IF NOT EXISTS 'user_answers' (
    'user_id' integer,
    'word_id' integer,
    'correct_answer_count' integer,
    'updated_at' timestamp,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(word_id) REFERENCES words(id)
    );
"""

const val CREATE_TABLE_USERS = """
    CREATE TABLE IF NOT EXISTS 'users' (
    'id' integer PRIMARY KEY,
    'username' varchar,
    'created_at' timestamp,
    'chat_id' integer
    );
"""

fun updateDictionary(wordsFile: File, statement: Statement) {

    val insertWords = wordsFile.readLines()
        .mapIndexed { index, line ->
            val (word, translate) = line.split("|")
            "($index, '${word}', '${translate}')"
        }.joinToString(",\n")

    statement.executeUpdate(
        """
        INSERT OR REPLACE INTO words 
        VALUES
        $insertWords
    """.trimIndent()
    )
}