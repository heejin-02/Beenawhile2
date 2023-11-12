package com.example.beenawhile.chat.ui
import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.beenawhile.chat.data.Conversation
import com.example.beenawhile.chat.data.Message
import com.google.firebase.database.*

class FirebaseDataFetcher(private val _conversation: MutableState<Conversation>) {

    fun fetchData(chatRoomId: String, callback: (List<Message>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(chatRoomId)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<Message>()

                // 데이터베이스의 모든 자식 노드를 가져옴
                snapshot.children.forEach { childSnapshot ->
                    // 각 자식 노드의 데이터를 가져와 Message 객체로 변환하여 리스트에 추가
                    val message = childSnapshot.getValue(Message::class.java)
                    message?.let {
                        dataList.add(it)
                    }
                }

                // LiveData를 통해 UI에 데이터를 전달
                updateConversation(dataList)
                callback(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
                Log.e("FirebaseData", "Error fetching data", error.toException())
            }
        })
    }

    private fun updateConversation(dataList: List<Message>) {
        val conversation = Conversation(list = dataList)
        _conversation.value = conversation
    }
}





