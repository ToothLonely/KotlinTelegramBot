package org.example

const val TEXT_TEMPLATE = "\"text\":\"(.+?)\""
const val UPDATE_ID_TEMPLATE = "\"update_id\":(\\d+)"
const val CHAT_ID_TEMPLATE = "\"chat\":\\{\"id\":(\\d+)"

fun main(args: Array<String>) {

    var updateId = 0
    val textRegex = TEXT_TEMPLATE.toRegex()
    val updateIdRegex = UPDATE_ID_TEMPLATE.toRegex()
    val chatIdRegex = CHAT_ID_TEMPLATE.toRegex()
    val telegramBotService = TelegramBotService(args[0])

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBotService.getUpdates(updateId)
        println(updates)

        updateId = getValueByRegex(updateIdRegex, updates)?.toInt() ?: continue
        val text = getValueByRegex(textRegex, updates)
        val chatId = getValueByRegex(chatIdRegex, updates)

        telegramBotService.sendMessage(chatId, text)

        updateId++
    }
}

fun getValueByRegex(template: Regex, text: String): String? {
    val matchResult = template.find(text)
    val value = matchResult?.groups?.get(1)?.value
    return value
}