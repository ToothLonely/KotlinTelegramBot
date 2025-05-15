package org.example

import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets

const val TELEGRAM_API_URL = "https://api.telegram.org/bot"

class TelegramBotService(
    private val botToken: String
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val client = HttpClient.newBuilder().build()
    private val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage"

    fun getUpdates(updateId: Int): List<Update> {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        var result: List<Update>

        try {
            val getUpdatesResponseString = client.send(getUpdatesRequest, BodyHandlers.ofString()).body()
            result = json.decodeFromString<Response>(getUpdatesResponseString).result
        } catch (e: IOException) {
            result = emptyList()
        }

        return result
    }

    fun sendMessage(text: String?, chatId: Long) {
        val encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val urlSendMessage = "$urlSendMessage?chat_id=$chatId&text=$encodedText"
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(sendMessageRequest, BodyHandlers.ofString())
    }

    fun sendMenu(chatId: Long) {
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = TELEGRAM_MENU,
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(LEARN_WORDS, LEARN_WORDS),
                        InlineKeyboard(STATISTIC, STATISTIC)
                    ),
                    listOf(
                        InlineKeyboard(RESET_PROGRESS, RESET_PROGRESS)
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        sendPOST(urlSendMessage, requestBodyString)
    }

    fun sendQuestion(question: Questions, chatId: Long) {
        val inlineKeyboardVariants = question.variants.mapIndexed { index, word ->
            InlineKeyboard(
                text = word.russianWord,
                callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
            )
        }
        val inlineKeyboardMenu = listOf(InlineKeyboard(TELEGRAM_MENU, MENU_COMMAND))
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "${question.correctAnswer.englishWord}: ",
            replyMarkup = ReplyMarkup(listOf(inlineKeyboardVariants, inlineKeyboardMenu))
        )
        val requestBodyString = json.encodeToString(requestBody)

        sendPOST(urlSendMessage, requestBodyString)

    }

    private fun sendPOST(url: String, body: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        client.send(request, BodyHandlers.ofString())
    }
}