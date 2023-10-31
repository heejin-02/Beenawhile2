// ChatRoomList.kt
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.beenawhile.chat.data.ChatRoom
import com.example.beenawhile.chat.ui.ChatRoomItem

@Composable
fun ChatRoomList(chatRooms: List<ChatRoom>, onItemClick: (ChatRoom) -> Unit) {
    LazyColumn {
        items(chatRooms) { chatRoom ->
            ChatRoomItem(chatRoom = chatRoom, onItemClick = { onItemClick(chatRoom) })
        }
    }
}
