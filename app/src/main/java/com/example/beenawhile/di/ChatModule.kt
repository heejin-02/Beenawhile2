package com.example.beenawhile.di

import androidx.lifecycle.MutableLiveData
import com.example.beenawhile.chat.data.Conversation
import com.example.beenawhile.chat.data.ConversationRepository
import com.example.beenawhile.chat.data.api.OpenAIRepository
import com.example.beenawhile.chat.domain.usecase.ObserveMessagesUseCase
import com.example.beenawhile.chat.domain.usecase.ResendMessageUseCase
import com.example.beenawhile.chat.domain.usecase.SendChatRequestUseCase
import com.example.beenawhile.chat.ui.ChatViewModel
import com.example.beenawhile.chat.ui.FirebaseDataFetcher
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chatModule = module {
    viewModel {
        ChatViewModel(
            sendChatRequestUseCase = get(),
            resendChatRequestUseCase = get(),
            observeMessagesUseCase = get(),
            firebaseDataFetcher = get() // 이미 생성된 FirebaseDataFetcher를 주입
        )
    }
    single { OpenAIRepository(openAI = get()) }
    single { ConversationRepository() }
    single { SendChatRequestUseCase(openAIRepository = get(), conversationRepository = get()) }
    single { ResendMessageUseCase(openAIRepository = get(), conversationRepository = get()) }
    single { ObserveMessagesUseCase(conversationRepository = get()) }
    single<FirebaseDataFetcher> {
        FirebaseDataFetcher { messagesList ->
        }
    }
}
