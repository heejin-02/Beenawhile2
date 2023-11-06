import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.beenawhile.R
import com.example.beenawhile.chat.data.Conversation
import com.example.beenawhile.chat.data.Message
import com.example.beenawhile.chat.data.MessageStatus
import com.example.beenawhile.utils.HorizontalSpacer
import com.example.beenawhile.utils.VerticalSpacer
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


data class ChatScreenUiHandlers(
    val onSendMessage: (String) -> Unit = {},
    val onResendMessage: (Message) -> Unit = {}
)
val chatRoomId = "1"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatRoomId: String,
    uiHandlers: ChatScreenUiHandlers = ChatScreenUiHandlers(),
    conversation: LiveData<Conversation>,
    isSendingMessage: LiveData<Boolean>,
    onBackClicked:() -> Unit //뒤로가기
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var inputValue by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val conversationState by conversation.observeAsState()
    val isSendingMessageState by isSendingMessage.observeAsState()

    fun getCurrentTimeUsingDate(): String {
        val timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        dateFormat.timeZone = timeZone
        val currentDate = Date()

        val formattedDate = dateFormat.format(currentDate)
        return formattedDate
    }

    val currentRoomNum = RoomNumInstance.instance.roomnum //현재 채팅방의 id를 가져옴

    // Firebase Realtime Database의 "messages" 레퍼런스를 가져옴
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference(currentRoomNum)

    fun sendMessage() {
        // Firebase에 데이터를 쓰기 위한 데이터 모델을 생성
        val messageText = inputValue
        val currentTime = getCurrentTimeUsingDate()

        val messageData = hashMapOf(
            "message" to messageText,
            "time" to currentTime,
            "isFromUser" to true
            // 여기에 다른 필요한 데이터도 추가할 수 있음
        )

        // "messages" 레퍼런스에 데이터를 저장
        myRef.push().setValue(messageData)

        uiHandlers.onSendMessage(inputValue)
        inputValue = ""
        coroutineScope.launch {
            listState.animateScrollToItem(conversationState?.list?.size ?: 0)
        }
    }
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat Title") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // 뒤로가기 버튼 클릭 시 채팅방 목록 화면으로 이동
                            onBackClicked()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues = paddingValues)
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                conversationState?.let {
                    MessageList(
                        messagesList = it.list,
                        listState = listState,
                        onResendMessage = uiHandlers.onResendMessage
                    )
                }
            }
            Row {
                TextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions {
                        sendMessage()
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                HorizontalSpacer(8.dp)
                Button(
                    modifier = Modifier.height(56.dp),
                    onClick = { sendMessage() },
                    enabled = inputValue.isNotBlank() && isSendingMessageState != true,
                ) {
                    if (isSendingMessageState == true) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sending"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send"
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun MessageList(
    messagesList: List<Message>,
    listState: LazyListState,
    onResendMessage: (Message) -> Unit
) {
    LazyColumn(
        state = listState
    ) {
        items(messagesList) { message ->
            Row {
                if (message.isFromUser) {
                    HorizontalSpacer(width = 16.dp)
                    Box(
                        modifier = Modifier.weight(weight = 1f)
                    )
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    textAlign = if (message.isFromUser) { TextAlign.End } else { TextAlign.Start },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (message.messageStatus == MessageStatus.Error) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                if (message.isFromUser) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.primaryContainer
                                }
                            }
                        )
                        .clickable(enabled = message.messageStatus == MessageStatus.Error) {
                            onResendMessage(message)
                        }
                        .padding(all = 8.dp)

                )
                if (!message.isFromUser) {
                    HorizontalSpacer(width = 16.dp)
                    Box(
                        modifier = Modifier.weight(weight = 1f)
                    )
                }
            }
            if (message.messageStatus == MessageStatus.Sending) {
                Text(
                    text = stringResource(R.string.chat_message_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalSpacer(width = 32.dp)
            }
            if (message.messageStatus == MessageStatus.Error) {
                Row(
                    modifier = Modifier
                        .clickable {
                            onResendMessage(message)
                        }
                ) {
                    Box(
                        modifier = Modifier.weight(weight = 1f)
                    )
                    Text(
                        text = stringResource(R.string.chat_message_error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            VerticalSpacer(height = 8.dp)
        }
    }
}

object FirebaseDataFetcher {
    fun fetchData() {
        val currentRoomNum = RoomNumInstance.instance.roomnum
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(currentRoomNum)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<Pair<String, DataSnapshot>>()

                // 데이터베이스의 모든 자식 노드를 가져옴
                snapshot.children.forEach { childSnapshot ->
                    // 각 자식 노드의 "time" 속성 값을 가져와 Pair에 저장
                    val time = childSnapshot.child("time").getValue(String::class.java)
                    time?.let {
                        dataList.add(it to childSnapshot)
                    }
                }

                // 데이터를 시간대순으로 정렬
                dataList.sortBy { it.first }

                // 정렬된 데이터를 로그에 출력
                for ((_, childSnapshot) in dataList) {
                    Log.d("FirebaseData", "Data: ${childSnapshot.value}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
                Log.e("FirebaseData", "Error fetching data", error.toException())
            }
        })
    }
}