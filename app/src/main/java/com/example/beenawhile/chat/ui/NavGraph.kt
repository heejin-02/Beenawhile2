package com.example.beenawhile.chat.ui

import ChatListScreen
import ChatScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.*
import chatRooms

@Composable
fun NavGraph(startDestination: String = "chatList") {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("chatList") {
            // ChatListScreen을 표시
            ChatListScreen(
                chatRooms = chatRooms, // chatRooms 데이터 추가
                onChatRoomClicked = { chatRoomId ->
                    navController.navigate("chatRoom/$chatRoomId")
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
                // ChatRoom 화면을 표시
                ChatScreen(
                    chatRoomId = chatRoomId,
                    conversation = chatViewModel.conversation, // conversation 데이터 추가 (LiveData 객체 그대로 전달)
                    isSendingMessage = chatViewModel.isSendingMessage, // isSendingMessage 데이터 추가 (LiveData 객체 그대로 전달)
                    onBackClicked = { navController.popBackStack() } // onBackClicked 데이터 추가
                )
            }

        }
    }
}
