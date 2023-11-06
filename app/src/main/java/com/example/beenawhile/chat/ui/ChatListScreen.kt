
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beenawhile.chat.data.ChatRoom
import com.example.beenawhile.chat.ui.ChatRoomItem


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
    val chatRoomsState = remember { mutableStateListOf(*chatRooms.toTypedArray()) } // chatRooms를 변경 가능한 State로 초기화
    LazyColumn {
        items(chatRooms) { chatRoom ->
            ChatRoomItem(
                chatRoom = chatRoom,
                onItemClick = {
                    onChatRoomClicked(chatRoom.id) // 채팅방 클릭 시 ChatScreen으로 이동
                    RoomNumInstance.instance.roomnum = chatRoom.id //roomnum 변수의 값을 채팅방 id로 설정
                    FirebaseDataFetcher.fetchData() //fetchData 함수를 실행하여, 저장되어있는 채팅 내역 출력
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
                chatRoomsState.add(ChatRoom((chatRoomsState.size + 1).toString(), "Chat Room ${chatRoomsState.size + 1}"))
                onCreateChatRoomClicked()
            },
            content = {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Chat Room")
            }
        )
    }

}
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
