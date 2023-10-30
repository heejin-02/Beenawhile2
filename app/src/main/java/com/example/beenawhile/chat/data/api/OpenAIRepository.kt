package com.example.beenawhile.chat.data.api

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.beenawhile.chat.data.Conversation
import com.example.beenawhile.chat.data.Message
import com.example.beenawhile.chat.data.MessageStatus
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@OptIn(BetaOpenAI::class)
class OpenAIRepository(private val openAI: OpenAI) {

    @Throws(NoChoiceAvailableException::class)
    suspend fun sendChatRequest(
        conversation: Conversation
    ) : Message {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = conversation.toChatMessages()
        )

        val chatMessage = openAI.chatCompletion(chatCompletionRequest).choices.first().message
            ?: throw NoChoiceAvailableException()

        // Firebase Realtime Database의 "messages" 레퍼런스를 가져옴
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("messages")

        // Firebase에 데이터를 쓰기 위한 데이터 모델을 생성
        val currentTime = getCurrentTimeUsingDate()

        val messageData = hashMapOf(
            "message" to chatMessage.content,
            "time" to currentTime,
            "isFromUser" to false
            // 여기에 다른 필요한 데이터도 추가할 수 있음
        )

        // "messages" 레퍼런스에 데이터를 저장
        myRef.push().setValue(messageData)

        return Message(
            text = chatMessage.content,
            isFromUser = chatMessage.role == ChatRole.User,
            messageStatus = MessageStatus.Sent
        )
    }

    private fun Conversation.toChatMessages() = this.list
        .filterNot { it.messageStatus == MessageStatus.Error }
        .map {
            ChatMessage(
                content = it.text,
                role = if (it.isFromUser) { ChatRole.User } else { ChatRole.Assistant }
            )
        }
}

class NoChoiceAvailableException: Exception()

private fun getCurrentTimeUsingDate(): String {
    val timeZone = TimeZone.getTimeZone("Asia/Seoul")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
    dateFormat.timeZone = timeZone
    val currentDate = Date()

    val formattedDate = dateFormat.format(currentDate)
    return formattedDate
}
