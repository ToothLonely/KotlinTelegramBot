package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

const val STRING_BEFORE_UPDATE_ID = "update_id"
const val STRING_AFTER_UPDATE_ID = ",\n\"message\""
const val UPDATE_ID_WORD_LENGTH = 11
const val OUT_OF_RANGE_ERROR = -1

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates = getUpdates(botToken, updateId)
        println(updates)

        val startUpdateId = updates.lastIndexOf(STRING_BEFORE_UPDATE_ID)
        val endUpdateId = updates.lastIndexOf(STRING_AFTER_UPDATE_ID)
        if (startUpdateId == OUT_OF_RANGE_ERROR || endUpdateId == OUT_OF_RANGE_ERROR) continue
        val updateIdString = updates.substring(startUpdateId + UPDATE_ID_WORD_LENGTH, endUpdateId)

        updateId = updateIdString.toInt() + 1
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val getUpdatesResponse = client.send(getUpdatesRequest, BodyHandlers.ofString())

    return getUpdatesResponse.body()
}