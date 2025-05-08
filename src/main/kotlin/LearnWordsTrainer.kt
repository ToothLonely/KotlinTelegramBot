package org.example

import java.io.File

data class Statistics(
    val learnedCount: Int,
    val totalCount: Int,
    val percent: Double,
)

data class Questions(
    var variants: List<Word>,
    val correctAnswer: Word,
)

data class Word(
    val englishWord: String,
    val russianWord: String,
    var correctAnswerCount: Int = 0
)

class LearnWordsTrainer {

    private val dictionary = loadDictionary()
    private val minimumCorrectAnswers = 3
    private val numberOfWordsToAnswer = 4
    lateinit var question: Questions

    private fun loadDictionary(): List<Word> {
        try {

            val words = File(FILENAME)
            val dictionary = mutableListOf<Word>()

            words.readLines().forEach {
                val line = it.split('|')
                val word = Word(line[0], line[1], line.getOrNull(2)?.toIntOrNull() ?: 0)

                dictionary.add(word)
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Не удалось загрузить словарь")
        }
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File(FILENAME)
        wordsFile.writeText("")
        dictionary.forEach { word ->
            wordsFile.appendText("${word.englishWord}|${word.russianWord}|${word.correctAnswerCount}\n")
        }
    }

    fun getStatistic(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswerCount >= minimumCorrectAnswers }.size
        val totalCount = dictionary.size
        val percent = (learnedCount.toDouble() / totalCount) * ONE_HUNDRED_PERCENT
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Questions? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < minimumCorrectAnswers }
        if (notLearnedList.isEmpty()) return null

        var questionWords = notLearnedList.shuffled().take(numberOfWordsToAnswer)
        val answeredWord = questionWords.random()

        if (questionWords.size <= numberOfWordsToAnswer) {
            val numberOfWordsToAdd = numberOfWordsToAnswer - questionWords.size
            val learnedWords = dictionary
                .filter { it.correctAnswerCount >= minimumCorrectAnswers }
                .shuffled()
                .take(numberOfWordsToAdd)
            questionWords = questionWords.plus(learnedWords)
        }

        question = Questions(questionWords, answeredWord)
        return question
    }

    fun checkAnswer(userAnswerInput: Int): Boolean {
        val correctAnswerId =
            question.variants.indexOf(question.correctAnswer)
        when (userAnswerInput) {
            correctAnswerId -> {
                question.correctAnswer.correctAnswerCount++
                saveDictionary(dictionary)
                return true
            }

            else -> return false
        }
    }
}
