package com.example.beenawhile.chat.ui

import ChatListScreen
import ChatScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import com.example.beenawhile.chat.data.ChatRoom
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(
    startDestination: String = "chatList",
    chatRooms: List<ChatRoom>,
) {
    val navController = rememberNavController()
    val chatRoomsState = remember { mutableStateListOf(*chatRooms.toTypedArray()) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("chatList") {
            ChatListScreen(
                chatRooms = chatRoomsState,
                onChatRoomClicked = { chatRoomId ->
                    navController.navigate("chatRoom/$chatRoomId")
                },
                onCreateChatRoomClicked = {
                    // 새 채팅방을 추가할 때, chatRoomsState를 업데이트
                    val newChatRoom = ChatRoom((chatRoomsState.size + 1).toString(), "Chat Room ${chatRoomsState.size + 1}")
                    chatRoomsState.add(newChatRoom)
                }
            )
        }

        composable(
            route = "chatRoom/{chatRoomId}",
            arguments = listOf(navArgument("chatRoomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatRoomId = backStackEntry.arguments?.getString("chatRoomId")
            val chatViewModel = viewModel<ChatViewModel>() // ChatViewModel 가져오기

            if (chatRoomId != null) {
                ChatScreen(
                    chatRoomId = chatRoomId,
                    conversation = chatViewModel.conversation,
                    isSendingMessage = chatViewModel.isSendingMessage,
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }
    }
}
