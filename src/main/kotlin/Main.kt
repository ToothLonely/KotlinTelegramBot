package org.example

import java.io.File

fun main() {
    val words = File("words.txt")
    val dictionary = mutableListOf<Word>()

    words.readLines().forEach {
        val line = it.split('|')
        val word = Word(line[0], line[1])

        if (line.size == 3) {
            word.correctAnswerCount = line[2].toInt()
        } else {
            word.correctAnswerCount = 0
        }

        dictionary.add(word)
    }

    dictionary.forEach { println(it) }
}