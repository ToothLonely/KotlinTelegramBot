package org.example

const val TEXT_TEMPLATE = "\"text\":\"(.+?)\""
const val UPDATE_ID_TEMPLATE = "\"update_id\":(\\d+)"
const val CHAT_ID_TEMPLATE = "\"chat\":\\{\"id\":(\\d+)"
const val DATA_TEMPLATE = "\"data\":\"(.+?)\""
const val TELEGRAM_MENU = "Меню:"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

fun main(args: Array<String>) {

    var updateId = 0
    val textRegex = TEXT_TEMPLATE.toRegex()
    val updateIdRegex = UPDATE_ID_TEMPLATE.toRegex()
    val chatIdRegex = CHAT_ID_TEMPLATE.toRegex()
    val dataRegex = DATA_TEMPLATE.toRegex()
    val telegramBotService = TelegramBotService(args[0])

    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBotService.getUpdates(updateId)
        println(updates)

        updateId = getValueByRegex(updateIdRegex, updates)?.toInt() ?: continue
        val text = getValueByRegex(textRegex, updates)
        val chatId = getValueByRegex(chatIdRegex, updates) ?: continue
        val menuData = getValueByRegex(dataRegex, updates)

        if (text?.lowercase() == "/start" || text?.lowercase() == "/menu") telegramBotService.sendMenu(chatId)


        when (menuData?.toInt()) {
            LEARN_WORDS_POINT -> checkNextQuestionAndSend(trainer, telegramBotService, chatId)

            STATISTIC_POINT -> {
                val statistic = trainer.getStatistic()
                val statisticMessage =
                    "Выучено ${statistic.learnedCount} из ${statistic.totalCount} слов | ${statistic.percent}%"
                telegramBotService.sendMessage(chatId, statisticMessage)
            }
        }

        updateId++
    }
}

fun getValueByRegex(template: Regex, text: String): String? {
    val matchResult = template.find(text)
    val value = matchResult?.groups?.get(1)?.value
    return value
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: String
) {
    val questions = trainer.getNextQuestion()
    if (questions == null) telegramBotService.sendMessage(chatId, "Все слова уже выучены")
    else telegramBotService.sendQuestion(chatId, questions)
}