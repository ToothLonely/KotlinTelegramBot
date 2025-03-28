package org.example

data class Word(
    val englishWord: String,
    val russianWord: String,
    var correctAnswerCount: Int = 0
)
