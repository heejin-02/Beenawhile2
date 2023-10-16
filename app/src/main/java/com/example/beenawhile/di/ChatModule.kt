package com.example.beenawhile.di

import com.example.beenawhile.chat.data.ConversationRepository
import com.example.beenawhile.chat.data.api.OpenAIRepository
import com.example.beenawhile.chat.domain.usecase.ObserveMessagesUseCase
import com.example.beenawhile.chat.domain.usecase.ResendMessageUseCase
import com.example.beenawhile.chat.domain.usecase.SendChatRequestUseCase
import com.example.beenawhile.chat.ui.ChatViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val chatModule = module {
    viewModel {
        ChatViewModel(get(), get(), get())
    }
    single { OpenAIRepository(openAI = get()) }
    single { ConversationRepository() }

    single { SendChatRequestUseCase(openAIRepository = get(), conversationRepository = get()) }
    single { ResendMessageUseCase(openAIRepository = get(), conversationRepository = get()) }
    single { ObserveMessagesUseCase(conversationRepository = get()) }
}