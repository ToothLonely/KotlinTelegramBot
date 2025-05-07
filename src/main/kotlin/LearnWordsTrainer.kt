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
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        dictionary.forEach { word ->
            wordsFile.appendText("${word.englishWord}|${word.russianWord}|${word.correctAnswerCount}\n")
        }
    }

    fun getStatistic(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswerCount >= MINIMUM_CORRECT_ANSWERS }.size
        val totalCount = dictionary.size
        val percent = (learnedCount.toDouble() / totalCount) * ONE_HUNDRED_PERCENT
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Questions? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < MINIMUM_CORRECT_ANSWERS }
        if (notLearnedList.isEmpty()) return null
        var questionWords = notLearnedList.shuffled().take(NUMBER_OF_WORDS_TO_ANSWER)
        val answeredWord = questionWords.random()
        if (questionWords.size <= NUMBER_OF_WORDS_TO_ANSWER) {
            val numberOfWordsToAdd = NUMBER_OF_WORDS_TO_ANSWER - questionWords.size
            val learnedWords = dictionary
                .filter { it.correctAnswerCount >= MINIMUM_CORRECT_ANSWERS }
                .shuffled()
                .take(numberOfWordsToAdd)
            questionWords = questionWords.plus(learnedWords)
        }

        return Questions(questionWords, answeredWord)
    }

    fun checkAnswer(userAnswerInput: Int, questions: Questions): Boolean {
        val correctAnswerId =
            questions.variants.indexOf(questions.correctAnswer)
        when (userAnswerInput) {
            correctAnswerId -> {
                questions.correctAnswer.correctAnswerCount++
                saveDictionary(dictionary)
                return true
            }

            else -> return false
        }
    }
}
