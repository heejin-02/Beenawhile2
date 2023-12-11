package com.example.beenawhile.chat.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chatRoomId
import com.example.beenawhile.chat.data.Conversation
import com.example.beenawhile.chat.data.Message
import com.example.beenawhile.chat.data.MessageStatus
import com.example.beenawhile.chat.domain.usecase.ObserveMessagesUseCase
import com.example.beenawhile.chat.domain.usecase.ResendMessageUseCase
import com.example.beenawhile.chat.domain.usecase.SendChatRequestUseCase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val resendChatRequestUseCase: ResendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val firebaseDataFetcher: FirebaseDataFetcher,
) : ViewModel() {

    private val _conversation = MutableLiveData<Conversation>()
    val conversation: LiveData<Conversation> = _conversation

    private val _isSendingMessage = MutableLiveData<Boolean>()
    val isSendingMessage: LiveData<Boolean> = _isSendingMessage

    init {
        updateConversationFromFirebase()
        fetchDataAndUpdate()
        observeMessageList()
    }

    private fun observeMessageList() {
        viewModelScope.launch {
            observeMessagesUseCase.invoke().collect { conversation ->
                _conversation.postValue(conversation)

                _isSendingMessage.postValue(
                    conversation.list.any { it.messageStatus == MessageStatus.Sending }
                )
            }
        }
    }

    fun sendMessage(chatRoomId: String, prompt: String) {
        viewModelScope.launch {
            sendChatRequestUseCase(
                chatRoomId,
                prompt
            )
        }
    }

    fun resendMessage(message: Message) {
        viewModelScope.launch {
            resendChatRequestUseCase(
                message
            )
        }
    }

    // Firebase에서 데이터를 가져와서 LiveData를 업데이트하는 함수
    private fun updateConversationFromFirebase() {
        firebaseDataFetcher.fetchDataAndUpdate(chatRoomId)
    }
    private fun fetchDataAndUpdate() {
        // 이전 데이터를 불러오기 위해 fetchAndUpdate 호출
        firebaseDataFetcher.fetchDataAndUpdate(chatRoomId)
    }
}
