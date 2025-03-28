package org.example

import java.io.File

class Menu {
    fun show() {
        println(
            """
                Меню:
                1 - Учить слова
                2 - Статистика
                3 - Выход
                Выберите один из вариантов (1, 2 или 0)
            """.trimIndent()
        )
    }

    fun loadDictionary(fileName: String): List<Word> {
        val words = File(fileName)
        val dictionary = mutableListOf<Word>()

        words.readLines().forEach {
            val line = it.split('|')
            val word = Word(line[0], line[1], line.getOrNull(2)?.toIntOrNull() ?: 0)

            dictionary.add(word)
        }

        return dictionary
    }
}