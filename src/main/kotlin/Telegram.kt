package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

fun main(args: Array<String>) {

    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates"

    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val request1 = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val response = client.send(request, BodyHandlers.ofString())
    val response1 = client.send(request1, BodyHandlers.ofString())

    println(response.body())
    println(response1.body())
}