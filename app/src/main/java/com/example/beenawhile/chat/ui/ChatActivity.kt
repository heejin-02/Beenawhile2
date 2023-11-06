package com.example.beenawhile.chat.ui

import ChatListScreen
import ChatScreen
import ChatScreenUiHandlers
import CreateChatRoomDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.beenawhile.chat.data.ChatRoom
import com.example.beenawhile.ui.ChatGptBotAppTheme
import org.koin.androidx.viewmodel.ext.android.stateViewModel


class ChatActivity : ComponentActivity() {

    private var onCreateChatRoomClicked by mutableStateOf(false) // onCreateChatRoomClicked 변수를 클래스 수준에 선언

    val chatRooms: MutableList<ChatRoom> = mutableListOf() // chatRooms를 MutableList로 선언

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
                            },
                            onCreateChatRoomClicked = {
                                // '+ 버튼' 클릭 시 실행될 코드
                                onCreateChatRoomClicked = true
                            }
                        )
                    }
                    composable(
                        "chatRoom/{chatRoomId}",
                        arguments = listOf(navArgument("chatRoomId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        // ChatScreen 내용은 그대로 두시고 onCreateChatRoomClicked 변수만 추가
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
                            },

                        )
                    }
                }
                // 추가: CreateChatRoomDialog를 Create Chat Room 버튼 클릭 시 표시
                CreateChatRoomDialog(
                    showDialog = onCreateChatRoomClicked,
                    onDialogDismiss = {
                        onCreateChatRoomClicked = false
                    },
                    onCreateChatRoom = {
                        // 새로운 채팅방 생성 및 chatRooms 목록에 추가하는 로직 추가
                        // ChatRoom 객체를 생성하고 chatRooms 목록에 추가
                        val newChatRoom = ChatRoom((chatRooms.size + 1).toString(), "Chat Room ${chatRooms.size + 1}")
                        chatRooms.add(newChatRoom)
                    }
                )
            }
        }
    }
}