package org.example

const val ONE_HUNDRED_PERCENT = 100

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

class LearnWordsTrainer(private val userName: String = "words.txt") {

    private val dictionary = DatabaseUserDictionary()
    private val numberOfWordsToAnswer = 4
    lateinit var question: Questions

    fun getStatistic(): Statistics {
        val learnedCount = dictionary.getNumOfLearnedWords(userName)
        val totalCount = dictionary.getSize()
        val percent = (learnedCount.toDouble() / totalCount) * ONE_HUNDRED_PERCENT
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Questions? {
        val notLearnedList = dictionary.getUnlearnedWords(userName)
        if (notLearnedList.isEmpty()) return null

        var questionWords = notLearnedList.shuffled().take(numberOfWordsToAnswer)
        val answeredWord = questionWords.random()

        if (questionWords.size <= numberOfWordsToAnswer) {
            val numberOfWordsToAdd = numberOfWordsToAnswer - questionWords.size
            val learnedWords = dictionary
                .getLearnedWords(userName)
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
        val correctWord = question.correctAnswer.englishWord
        when (userAnswerInput) {
            correctAnswerId -> {
                dictionary.setCorrectAnswersCount(correctWord, ++question.correctAnswer.correctAnswerCount, userName)
                return true
            }

            else -> return false
        }
    }

    fun resetProgress() {
        dictionary.resetUserProgress(userName)
    }
}
