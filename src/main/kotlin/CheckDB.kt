package org.example

import java.sql.DriverManager

fun main() {

    val query = """
        SELECT ua.user_name, w.text, w.translate, ua.word_id, ua.correct_answers_count
        FROM user_answers AS ua
        JOIN words AS w ON ua.word_id = w.id 
    """.trimIndent()

    DriverManager.getConnection("jdbc:sqlite:data.db").use { connection ->
        val a = connection.createStatement().executeQuery(query)
        while(a.next()){
            println("${a.getObject(1)}, ${a.getObject(2)}, ${a.getObject(3)}, ${a.getObject(4)}, ${a.getObject(5)}")
        }
    }
}