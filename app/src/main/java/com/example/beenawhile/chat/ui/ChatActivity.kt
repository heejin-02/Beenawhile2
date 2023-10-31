package com.example.beenawhile.chat.ui

import ChatListScreen
import ChatScreen
import ChatScreenUiHandlers
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.beenawhile.chat.data.ChatRoom
import com.example.beenawhile.ui.ChatGptBotAppTheme
import com.example.beenawhile.chat.ui.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel


class ChatActivity : ComponentActivity() {

    val chatRooms: List<ChatRoom> = listOf(
        ChatRoom("1", "Chat Room 1"),
        ChatRoom("2", "Chat Room 2"),
        ChatRoom("3", "Chat Room 3"),
        ChatRoom("4", "Chat Room 4"),
        ChatRoom("5", "Chat Room 5"),
        ChatRoom("6", "Chat Room 6"),
        ChatRoom("7", "Chat Room 7"),
        ChatRoom("8", "Chat Room 8"),
        ChatRoom("9", "Chat Room 9"),
        ChatRoom("10", "Chat Room 10"),
        ChatRoom("11", "Chat Room 11"),
        ChatRoom("12", "Chat Room 12"),
        ChatRoom("13", "Chat Room 13"),
        ChatRoom("14", "Chat Room 14"),
        ChatRoom("15", "Chat Room 15"),
        ChatRoom("16", "Chat Room 16")

    )

    private val viewModel: ChatViewModel by stateViewModel(
        state = { intent?.extras ?: Bundle() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatGptBotAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "chatList"
                ) {
                    composable("chatList") {
                        ChatListScreen(
                            chatRooms = chatRooms,
                            onChatRoomClicked = { chatRoomId ->
                                navController.navigate("chatRoom/$chatRoomId")
                            }
                        )
                    }
                    composable(
                        "chatRoom/{chatRoomId}",
                        arguments = listOf(navArgument("chatRoomId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val chatRoomId = backStackEntry.arguments?.getString("chatRoomId")
                        ChatScreen(
                            chatRoomId = chatRoomId ?: "",
                            uiHandlers = ChatScreenUiHandlers(
                                onSendMessage = viewModel::sendMessage,
                                onResendMessage = viewModel::resendMessage
                            ),
                            conversation = viewModel.conversation,
                            isSendingMessage = viewModel.isSendingMessage,
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 여기서 앱이 활성화될 때마다 수행할 작업을 작성하십시오.
        Log.d("ActivityLifecycle", "앱이 활성화되었습니다.")
    }
}