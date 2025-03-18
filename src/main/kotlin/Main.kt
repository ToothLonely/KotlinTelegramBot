package org.example

import java.io.File

fun main() {
    val dictionary = File("words.txt")
    dictionary.readLines().forEach { println(it) }
}