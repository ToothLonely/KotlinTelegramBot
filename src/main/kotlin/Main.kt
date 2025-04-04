package org.example

import java.io.File

const val ONE_HUNDRED_PERCENT = 100
const val MINIMUM_CORRECT_ANSWERS = 3
const val DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX = 1
const val NUMBER_OF_WORDS_TO_ANSWER = 3

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
    val words = File(filename)
    var currentData = words.readText()

    words.readLines().forEach {
        val currentLine = it.split('|')
        val currentWord = Word(currentLine[0], currentLine[1], currentLine.getOrNull(2)?.toIntOrNull() ?: 0)

        for (word in dictionary) {
            if (word.englishWord == currentWord.englishWord && word.correctAnswerCount != currentWord.correctAnswerCount) {
                //Тут я добавляю |0 в тех словах, где их не было
                if (currentLine.size == 2) {
                    currentData = currentData.replace(it, "$it|0")
                    words.writeText(currentData)
                }

                val newLine =
                    it.replace(currentWord.correctAnswerCount.toString(), word.correctAnswerCount.toString())
                currentData = currentData.replace(it, newLine)
                words.writeText(currentData)

                break
            }
        }
    }
}

fun learnWords(dictionary: List<Word>) {
    outer@ while (true) {
        val notLearnedList = if (dictionary.none { it.correctAnswerCount < MINIMUM_CORRECT_ANSWERS }) {
            println("Все слова в словаре выучены")
            return
        } else {
            dictionary.filter { it.correctAnswerCount < MINIMUM_CORRECT_ANSWERS }
        }

        val questionWords = notLearnedList.shuffled().take(NUMBER_OF_WORDS_TO_ANSWER).toMutableList()

        for (currentWord in notLearnedList) {
            if (currentWord in questionWords) continue

            val correctAnswerId: Int
            var userAnswerInput = 0
            var userAnswerInputFlag = true

            questionWords.add(currentWord)
            val shuffledQuestionWords = questionWords.shuffled()
            correctAnswerId =
                shuffledQuestionWords.indexOf(currentWord) + DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX

            println("${currentWord.englishWord}: ")
            shuffledQuestionWords.forEachIndexed { index, word ->
                println("\t${index + DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX} - ${word.russianWord}")
            }
            println("\t----------")
            println("\t0 -  Меню")
            //println(correctAnswerId)

            while (userAnswerInputFlag) {
                try {
                    userAnswerInput = readln().toInt()
                    userAnswerInputFlag = false
                } catch (e: NumberFormatException) {
                    println("Ввести надо число")
                }
            }

            if (userAnswerInput == correctAnswerId) {
                println("Правильно!")
                currentWord.correctAnswerCount++
                saveDictionary(dictionary, "words.txt")
            } else if (userAnswerInput == 0) {
                break@outer
            } else {
                println("Неправильно! ${currentWord.englishWord} – это ${currentWord.russianWord}")
            }

            questionWords.remove(currentWord)
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