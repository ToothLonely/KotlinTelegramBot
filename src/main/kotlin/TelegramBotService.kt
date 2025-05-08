package org.example

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

    private val client = HttpClient.newBuilder().build()
    private val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage"

    fun getUpdates(updateId: Int): String {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val getUpdatesResponse = client.send(getUpdatesRequest, BodyHandlers.ofString())

        return getUpdatesResponse.body()
    }

    fun sendMessage(chatId: String, text: String?) {
        val encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val urlSendMessage = "$urlSendMessage?chat_id=$chatId&text=$encodedText"
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(sendMessageRequest, BodyHandlers.ofString())
    }

    fun sendMenu(chatId: String) {
        val menuBody = """
            {
                "chat_id": $chatId,
                "text": "$TELEGRAM_MENU",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Учить слова",
                                "callback_data": "$LEARN_WORDS_POINT"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTIC_POINT"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        sendPOST(urlSendMessage, menuBody)
    }

    fun sendQuestion(chatId: String, question: Questions) {
        val inlineKeyboardsVariants = question.variants.mapIndexed { index, word ->
            """
                {
                    "text": "${word.russianWord}",
                    "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX$index"
                }
            """.trimIndent()
        }.joinToString(separator = ",\n")
        val questionBody = """
            {
                "chat_id": $chatId,
                "text": "${question.correctAnswer.englishWord}: ",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            $inlineKeyboardsVariants
                        ]
                    ]
                }
            }
        """.trimIndent()
        sendPOST(urlSendMessage, questionBody)

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