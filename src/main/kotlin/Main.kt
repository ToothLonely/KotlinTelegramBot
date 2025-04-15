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

fun saveDictionary(dictionary: List<Word>, filename: String) {
    val wordsFile = File(filename)
    wordsFile.writeText("")
    dictionary.forEach { word ->
        wordsFile.appendText("${word.englishWord}|${word.russianWord}|${word.correctAnswerCount}\n")
    }
}

fun learnWords(dictionary: List<Word>) {
    while (true) {
        var userAnswerInput = 0
        var userAnswerInputFlag = true

        val notLearnedList = dictionary.filter { it.correctAnswerCount < MINIMUM_CORRECT_ANSWERS }
        if (notLearnedList.isEmpty()) {
            println("Все слова в словаре выучены")
            return
        }

        var questionWords = notLearnedList.shuffled().take(NUMBER_OF_WORDS_TO_ANSWER)
        val answeredWord = questionWords.random()
        val correctAnswerId = questionWords.indexOf(answeredWord) + DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX

        if (questionWords.size <= NUMBER_OF_WORDS_TO_ANSWER) {
            val numberOfWordsToAdd = NUMBER_OF_WORDS_TO_ANSWER - questionWords.size
            val learnedWords = dictionary
                .sortedBy { it.correctAnswerCount }
                .filter { it !in questionWords }
                .take(numberOfWordsToAdd)
            questionWords = questionWords.plus(learnedWords)
        }

        val variants = questionWords
            .mapIndexed { index: Int, word: Word -> " ${index + DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX} – ${word.russianWord}" }
            .joinToString(
                separator = "\n\t",
                prefix = "\n${answeredWord.englishWord}:\n\t",
                postfix = "\n\t ----------\n\t 0 – Меню",
            )

        println(variants)


        while (userAnswerInputFlag) {
            try {
                userAnswerInput = readln().toInt()
                userAnswerInputFlag = false
            } catch (e: NumberFormatException) {
                println("Ввести надо число")
            }
        }

        when (userAnswerInput) {
            correctAnswerId -> {
                println("Правильно!")
                answeredWord.correctAnswerCount++
                saveDictionary(dictionary, "words.txt")
            }

            0 -> break
            else -> {
                println("Неправильно! ${answeredWord.englishWord} - это ${answeredWord.russianWord}")
            }
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
                0 - Выход
                Выберите один из вариантов (1, 2 или 0)
            """.trimIndent()
        )

        when (readln()) {
            "1" -> {
                learnWords(dictionary)
            }

            "2" -> {
                showStatistic(dictionary)
            }

            "0" -> break

            else -> println("Введите число 1, 2 или 0")
        }
    }
}