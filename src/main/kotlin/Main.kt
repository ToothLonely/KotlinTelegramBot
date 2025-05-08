package org.example

const val FILENAME = "words.txt"
const val ONE_HUNDRED_PERCENT = 100
const val DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX = 1
const val LEARN_WORDS_POINT = "learn_words"
const val STATISTIC_POINT = "show_statistic"
const val EXIT_POINT = 0
const val MENU = """
                Меню:
                1 - Учить слова
                2 - Статистика
                0 - Выход
                Выберите один из вариантов (1, 2 или 0)
            """

fun Questions.transformToString(): String {
    val variants = this.variants
        .mapIndexed { index: Int, word: Word -> " ${index + DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX} – ${word.russianWord}" }
        .joinToString(
            separator = "\n\t",
            prefix = "\n${this.correctAnswer.englishWord}:\n\t",
            postfix = "\n\t ----------\n\t 0 – Меню",
        )

    return variants
}

fun showStatistic(trainer: LearnWordsTrainer) {
    val statistics = trainer.getStatistic()
    println("Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%")
    println()
}

fun makeCorrectInput(): Int {
    var userAnswerInput = 0
    var userAnswerInputFlag = true

    while (userAnswerInputFlag) {
        try {
            userAnswerInput = readln().toInt()
            userAnswerInputFlag = false
        } catch (e: NumberFormatException) {
            println("Ввести надо число")
        }
    }
    return userAnswerInput
}

fun learnWords(trainer: LearnWordsTrainer) {
    while (true) {
        val questions = trainer.getNextQuestion()

        if (questions == null) {
            println("Все слова выучены")
            break
        }
        println(questions.transformToString())

        val userAnswerInput = makeCorrectInput()

        if (userAnswerInput == EXIT_POINT) break

        when (trainer.checkAnswer(userAnswerInput)) {
            true -> println("Правильно!")
            false -> println("Неправильно! ${questions.correctAnswer.englishWord} - это ${questions.correctAnswer.russianWord}")
        }
    }
}

fun main() {
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Что-то пошло не так, но мы уже над этим работаем...")
        return
    }

    while (true) {
        println(MENU.trimIndent())

        when (readln()) {
            LEARN_WORDS_POINT -> {
                learnWords(trainer)
            }

            STATISTIC_POINT -> {
                showStatistic(trainer)
            }

            EXIT_POINT.toString() -> break

            else -> println("Введите число 1, 2 или 0")
        }
    }
}