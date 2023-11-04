package com.example.beenawhile.chat.ui

// CreateChatRoomDialog.kt

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CreateChatRoomDialog(
    showDialog: Boolean,
    onDialogDismiss: () -> Unit,
    onCreateChatRoom: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDialogDismiss() },
            title = { Text(text = "새 채팅방을 만드시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDialogDismiss()
                        onCreateChatRoom()
                    }
                ) {
                    Text("네")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDialogDismiss() }
                ) {
                    Text("아니오")
                }
            }
        )
    }
}
