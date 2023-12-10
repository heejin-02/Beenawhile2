import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.beenawhile.chat.data.ChatRoom
import com.example.beenawhile.chat.data.ChatRoomIdGenerator
import com.example.beenawhile.chat.ui.ChatRoomItem
import com.example.beenawhile.chat.ui.chatRooms
import com.google.firebase.database.*

val modelIdMap = mutableMapOf<String, String>()

class RoomNum{
    var roomnum: String = ""
}

object RoomNumInstance {
    val instance = RoomNum()
}

@Composable
fun ChatListScreen(
    chatRooms: List<ChatRoom>,
    onChatRoomClicked: (chatRoomId: String) -> Unit,
    onCreateChatRoomClicked: () -> Unit//버튼 클릭 이벤트
) {
    val chatRoomsState = remember { mutableStateListOf(*chatRooms.toTypedArray()) }

    LazyColumn {
        items(chatRooms) { chatRoom ->
            ChatRoomItem(
                chatRoom = chatRoom,
                onItemClick = {
                    onChatRoomClicked(chatRoom.id) // 채팅방 클릭 시 ChatScreen으로 이동
                    RoomNumInstance.instance.roomnum = chatRoom.id //roomnum 변수의 값을 채팅방 id로 설정

                }
            )
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                val newChatRoom = ChatRoom(
                    id = ChatRoomIdGenerator.generateId(),
                    name = "Chat Room ${chatRoomsState.size + 1}"
                )
                chatRoomsState.add(newChatRoom)
                onCreateChatRoomClicked()
            },
            content = {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Chat Room")
            }
        )
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChatRoomDialog(
    showDialog: Boolean,
    onDialogDismiss: () -> Unit,
    onCreateChatRoom: () -> Unit
) {
    var showNewDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDialogDismiss() },
            title = { Text(text = "새 채팅방을 만드시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDialogDismiss()
                        showNewDialog = true
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

    if (showNewDialog) {
        var modelId by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewDialog = false },
            title = { Text("대화할 모델의 ID를 입력하십시오.") },
            text = {
                Column {
                    TextField(
                        value = modelId,
                        onValueChange = { modelId = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNewDialog = false
                        modelIdMap[(chatRooms.size + 1).toString()] = modelId
                        onCreateChatRoom()
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNewDialog = false }
                ) {
                    Text("취소")
                }
            }
        )
    }
}