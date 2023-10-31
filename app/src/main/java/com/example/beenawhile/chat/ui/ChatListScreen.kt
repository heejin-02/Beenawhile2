import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.beenawhile.chat.data.ChatRoom
import com.example.beenawhile.chat.ui.ChatRoomItem

val chatRooms: List<ChatRoom> = listOf(
    ChatRoom("1", "Chat Room 1"),
    ChatRoom("2", "Chat Room 2"),
    ChatRoom("3", "Chat Room 3")
)

@Composable
fun ChatListScreen(
    chatRooms: List<ChatRoom>,
    onChatRoomClicked: (chatRoomId: String) -> Unit
) {
    LazyColumn {
        items(chatRooms) { chatRoom ->
            ChatRoomItem(
                chatRoom = chatRoom,
                onItemClick = {
                    onChatRoomClicked(chatRoom.id) // 채팅방 클릭 시 ChatScreen으로 이동
                }
            )
        }
    }
}
