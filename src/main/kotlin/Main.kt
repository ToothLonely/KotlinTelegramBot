package org.example

fun main() {
    val filename = "words.txt"
    val dictionary = Menu().loadDictionary(filename)

    while (true) {
        Menu().show()

        when (readln()) {
            "1" -> println("Вы выбрали учить слова")
            "2" -> println("Вы выбрали просмотреть статистику")
            "0" -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }
}