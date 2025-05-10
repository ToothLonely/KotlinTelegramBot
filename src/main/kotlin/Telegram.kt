package org.example

const val TELEGRAM_MENU = "Меню"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val MENU_COMMAND = "/menu"
const val START_COMMAND = "/start"

fun main(args: Array<String>) {

    var updateId = 0L
    val telegramBotService = TelegramBotService(args[0])
    val trainer = LearnWordsTrainer()
    val json = telegramBotService.json

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBotService.getUpdates(updateId.toInt())
        println(responseString)

        val response = json.decodeFromString<Response>(responseString)

        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue

        updateId = firstUpdate.updateId
        val text = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.chatId ?: firstUpdate.callbackQuery?.message?.chat?.chatId ?: continue
        val inputData = firstUpdate.callbackQuery?.data

        when {
            text?.lowercase() == START_COMMAND || text?.lowercase() == MENU_COMMAND || inputData == MENU_COMMAND -> {
                telegramBotService.sendMenu(chatId)
            }

            inputData == LEARN_WORDS -> {
                checkNextQuestionAndSend(trainer, telegramBotService, chatId)
            }

            inputData == STATISTIC -> {
                val statistic = trainer.getStatistic()
                val statisticMessage =
                    "Выучено ${statistic.learnedCount} из ${statistic.totalCount} слов | ${statistic.percent}%"
                telegramBotService.sendMessage(chatId, statisticMessage)
                Thread.sleep(1000)
                telegramBotService.sendMenu(chatId)
            }

            inputData?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
                val userAnswerInput = inputData.substringAfter(CALLBACK_DATA_ANSWER_PREFIX)
                when (trainer.checkAnswer(userAnswerInput.toInt())) {
                    true -> telegramBotService.sendMessage(chatId, "Правильно")
                    false -> telegramBotService.sendMessage(
                        chatId,
                        "Неправильно! ${trainer.question.correctAnswer.englishWord} - это ${trainer.question.correctAnswer.russianWord}"
                    )
                }
                Thread.sleep(1000)
                checkNextQuestionAndSend(trainer, telegramBotService, chatId)
            }
        }

        updateId++
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    val questions = trainer.getNextQuestion()
    if (questions == null) telegramBotService.sendMessage(chatId, "Все слова уже выучены")
    else telegramBotService.sendQuestion(chatId, questions)
}