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
    val textRegex = TEXT_TEMPLATE.toRegex()
    val updateIdRegex = UPDATE_ID_TEMPLATE.toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = getUpdates(botToken, updateId)
        println(updates)

        updateId = getValueByRegex(updateIdRegex, updates)?.toInt() ?: continue
        val text = getValueByRegex(textRegex, updates)
        println(updateId)
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

fun getValueByRegex(template: Regex, text: String): String? {
    val matchResult = template.find(text)
    val value = matchResult?.groups?.get(1)?.value
    return value
}