package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

class TelegramBotService {

    fun getUpdates(botToken: String, updateId: Int): String {
        val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val getUpdatesResponse = client.send(getUpdatesRequest, BodyHandlers.ofString())

        return getUpdatesResponse.body()
    }

    fun sendMessage(chatId: String?, text: String?, botToken: String) {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$text"
        val client = HttpClient.newBuilder().build()
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(sendMessageRequest, BodyHandlers.ofString())
    }

}