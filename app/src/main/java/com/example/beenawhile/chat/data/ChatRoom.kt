package com.example.beenawhile.chat.data

// ChatRoom.kt

data class ChatRoom(
    val id: String,
    val name: String
)
object ChatRoomIdGenerator {
    private var currentId = 1

    fun generateId(): String {
        val newId = currentId.toString()
        currentId++
        return newId
    }
}