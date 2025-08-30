package org.example

const val TELEGRAM_MENU = "Меню"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val MENU_COMMAND = "/menu"
const val START_COMMAND = "/start"

fun main(args: Array<String>) {

    var lastUpdateId = 0L
    val telegramBotService = TelegramBotService(args[0])
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBotService.getUpdates(lastUpdateId.toInt())
        if (updates.isEmpty()) continue
        println(updates)

        val sortedUpdates = updates.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, trainers, telegramBotService) }

        lastUpdateId = sortedUpdates.last().updateId + 1

    }
}

fun handleUpdate(update: Update, trainers: HashMap<Long, LearnWordsTrainer>, telegramBotService: TelegramBotService) {

    val text = update.message?.text
    val chatId = update.message?.chat?.chatId ?: update.callbackQuery?.message?.chat?.chatId ?: return
    val inputData = update.callbackQuery?.data
    val username = update.message?.chat?.username ?: update.callbackQuery?.message?.chat?.username ?: return

    val trainer = trainers.getOrPut(chatId) {
        DatabaseUserDictionary().addUser(username, chatId)
        LearnWordsTrainer(username)
    }

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
            telegramBotService.sendMessage(statisticMessage, chatId)
            telegramBotService.sendMenu(chatId)
        }

        inputData?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
            val userAnswerInput = inputData.substringAfter(CALLBACK_DATA_ANSWER_PREFIX)
            when (trainer.checkAnswer(userAnswerInput.toInt())) {
                true -> telegramBotService.sendMessage("Правильно", chatId)
                false -> telegramBotService.sendMessage(
                    "Неправильно! ${trainer.question.correctAnswer.englishWord} - это ${trainer.question.correctAnswer.russianWord}",
                    chatId
                )
            }
            Thread.sleep(1000)
            checkNextQuestionAndSend(trainer, telegramBotService, chatId)
        }

        inputData == RESET_PROGRESS -> {
            trainer.resetProgress()
            telegramBotService.sendMessage("Прогресс сброшен", chatId)
            telegramBotService.sendMenu(chatId)
        }
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long,
) {
    val questions = trainer.getNextQuestion()
    if (questions == null) telegramBotService.sendMessage("Все слова уже выучены", chatId)
    else telegramBotService.sendQuestion(questions, chatId)
}