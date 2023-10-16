package com.example.beenawhile.chat.domain.usecase

import com.example.beenawhile.chat.data.ConversationRepository

class ObserveMessagesUseCase(
    private val conversationRepository: ConversationRepository
) {

    operator fun invoke() = conversationRepository.conversationFlow

}