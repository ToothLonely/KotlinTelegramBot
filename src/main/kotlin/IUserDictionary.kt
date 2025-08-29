package org.example

interface IUserDictionary {
    fun getNumOfLearnedWords(userName: String): Int
    fun getSize(): Int
    fun getLearnedWords(userName: String): List<Word>
    fun getUnlearnedWords(userName: String): List<Word>
    fun setCorrectAnswersCount(word: String, correctAnswersCount: Int, userName: String)
    fun resetUserProgress(userName: String)
}