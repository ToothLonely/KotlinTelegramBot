package org.example

const val DIFFERENCE_BETWEEN_LIST_INDEX_AND_ANSWER_INDEX = 1
const val LEARN_WORDS = "Учить слова"
const val STATISTIC = "Статистика"
const val RESET_PROGRESS = "Сбросить статистику"
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
            LEARN_WORDS -> {
                learnWords(trainer)
            }

            STATISTIC -> {
                showStatistic(trainer)
            }

            EXIT_POINT.toString() -> break

            else -> println("Введите число 1, 2 или 0")
        }
    }
}