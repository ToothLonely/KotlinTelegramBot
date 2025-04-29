package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

const val TELEGRAM_API_URL = "https://api.telegram.org/bot"

class TelegramBotService(
    private val botToken: String
) {

    private val client = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val getUpdatesResponse = client.send(getUpdatesRequest, BodyHandlers.ofString())

        return getUpdatesResponse.body()
    }

    fun sendMessage(chatId: String?, text: String?) {
        val urlSendMessage = "$TELEGRAM_API_URL$botToken/sendMessage?chat_id=$chatId&text=$text"
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(sendMessageRequest, BodyHandlers.ofString())
    }

}