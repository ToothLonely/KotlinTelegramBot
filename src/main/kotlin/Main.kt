package org.example

import java.io.File

const val ONE_HUNDRED_PERCENT = 100
const val MINIMUM_CORRECT_ANSWERS = 3
const val DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX = 1
const val NUMBER_OF_WORDS_TO_ANSWER = 4

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
    val learnedCount = dictionary.filter { it.correctAnswerCount >= MINIMUM_CORRECT_ANSWERS }.size
    val totalCount = dictionary.size
    val percent = (learnedCount.toDouble() / totalCount) * ONE_HUNDRED_PERCENT

    println("Выучено $learnedCount из $totalCount слов | $percent%")
    println()
}

fun learnWords(dictionary: List<Word>) {
    while (true) {
        val notLearnedList = if (dictionary.none { it.correctAnswerCount < MINIMUM_CORRECT_ANSWERS }) {
            println("Все слова в словаре выучены")
            return
        } else {
            dictionary.filter { it.correctAnswerCount < MINIMUM_CORRECT_ANSWERS }
        }

        val questionWords = notLearnedList.shuffled().take(NUMBER_OF_WORDS_TO_ANSWER)

        for (i in notLearnedList) {
            println("${i.englishWord}: ")
            questionWords.shuffled().forEachIndexed { index, word ->
                println("\t${index + DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX} - ${word.russianWord}")
            }

            val answer = readln()
        }

    }
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
            "1" -> {
                println("Вы выбрали учить слова")
                learnWords(dictionary)
            }

            "2" -> {
                println("Вы выбрали просмотреть статистику")
                showStatistic(dictionary)
            }

            "0" -> break

            else -> println("Введите число 1, 2 или 0")
        }
    }
}