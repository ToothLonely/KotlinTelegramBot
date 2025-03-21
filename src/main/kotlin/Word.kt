package org.example

data class Word(
    private val englishWord: String,
    private val russianWord: String,
    var correctAnswerCount: Int = 0
)
