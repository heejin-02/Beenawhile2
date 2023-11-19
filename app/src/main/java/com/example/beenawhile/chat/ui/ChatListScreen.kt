import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beenawhile.chat.data.ChatRoom
import com.example.beenawhile.chat.data.ChatRoomIdGenerator
import com.example.beenawhile.chat.data.Message
import com.example.beenawhile.chat.ui.ChatRoomItem
import com.example.beenawhile.chat.ui.FirebaseDataFetcher
import com.google.firebase.database.*
import kotlinx.coroutines.launch


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
@Composable
fun updateUIWithData(dataList: List<Message>) {
    // 가져온 데이터를 사용하거나 처리하는 로직을 여기에 추가
    // 여기서는 간단히 로그를 출력하도록 했습니다.
    Log.d("ChatListScreen", "Data fetched: $dataList")
}
