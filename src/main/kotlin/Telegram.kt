package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

const val TEXT_TEMPLATE = "\"text\":\"(.+?)\""
const val UPDATE_ID_TEMPLATE = "\"update_id\":(\\d+)"

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates = getUpdates(botToken, updateId)
        println(updates)

        val updateIdRegex = UPDATE_ID_TEMPLATE.toRegex()
        val matchResultUpdateId = updateIdRegex.find(updates) ?: continue
        updateId = matchResultUpdateId.groups[1]!!.value.toInt()
        println(updateId)

        val messageTextRegex: Regex = TEXT_TEMPLATE.toRegex()
        val matchResultText = messageTextRegex.find(updates)
        val text = matchResultText?.groups?.get(1)?.value
        println(text)
        updateId++
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val getUpdatesResponse = client.send(getUpdatesRequest, BodyHandlers.ofString())

    return getUpdatesResponse.body()
}