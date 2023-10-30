package com.example.beenawhile.chat.ui

import ChatScreen
import ChatScreenUiHandlers
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import androidx.activity.compose.setContent
import com.example.beenawhile.ui.ChatGptBotAppTheme

class ChatActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by stateViewModel(
        state = { intent?.extras ?: Bundle() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatGptBotAppTheme {
                ChatScreen(
                    uiHandlers = ChatScreenUiHandlers(
                        onSendMessage = viewModel::sendMessage,
                        onResendMessage = viewModel::resendMessage
                    ),
                    conversation = viewModel.conversation,
                    isSendingMessage = viewModel.isSendingMessage
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 여기서 앱이 활성화될 때마다 수행할 작업을 작성하십시오.
        Log.d("ActivityLifecycle", "앱이 활성화되었습니다.")
    }
}