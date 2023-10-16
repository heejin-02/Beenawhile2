package com.example.beenawhile.chat.domain.usecase

import com.example.beenawhile.chat.data.ConversationRepository
import com.example.beenawhile.chat.data.Message
import com.example.beenawhile.chat.data.MessageStatus
import com.example.beenawhile.chat.data.api.OpenAIRepository

class ResendMessageUseCase(
    private val openAIRepository: OpenAIRepository,
    private val conversationRepository: ConversationRepository
) {

    suspend operator fun invoke(
        message: Message
    ) {
        val conversation = conversationRepository.resendMessage(message)

        try {
            val reply = openAIRepository.sendChatRequest(conversation)
            conversationRepository.setMessageStatusToSent(message.id)
            conversationRepository.addMessage(reply)
        } catch (exception: Exception) {
            conversationRepository.setMessageStatusToError(message.id)
        }
    }
}