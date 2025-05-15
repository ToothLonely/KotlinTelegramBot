package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val result: List<Update>
)

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
    val message: Message? = null
)

@Serializable
data class CallbackQuery(
    val data: String,
    val message: Message,
)

@Serializable
data class Message(
    val text: String,
    val chat: Chat? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val chatId: Long,
    val username: String
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>
)

@Serializable
data class InlineKeyboard(
    val text: String,
    @SerialName("callback_data")
    val callbackData: String
)