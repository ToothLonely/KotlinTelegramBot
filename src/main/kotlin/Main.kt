package org.example

import java.io.File

const val ONE_HUNDRED_PERCENT = 100

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

fun showStatistic(dictionary: List<Word>) {
    val learnedCount = dictionary.filter { it.correctAnswerCount >= 3 }.size
    val totalCount = dictionary.size
    val percent = (learnedCount.toDouble() / totalCount) * ONE_HUNDRED_PERCENT

    println("Выучено $learnedCount из $totalCount слов | $percent%")
    println()
}

fun main() {
    val filename = "words.txt"
    val dictionary = loadDictionary(filename)

    while (true) {
        println(
            """
                Меню:
                1 - Учить слова
                2 - Статистика
                3 - Выход
                Выберите один из вариантов (1, 2 или 0)
            """.trimIndent()
        )

        when (readln()) {
            "1" -> println("Вы выбрали учить слова")

            "2" -> {
                println("Вы выбрали просмотреть статистику")
                showStatistic(dictionary)
            }

            "0" -> break

            else -> println("Введите число 1, 2 или 0")
        }
    }
}