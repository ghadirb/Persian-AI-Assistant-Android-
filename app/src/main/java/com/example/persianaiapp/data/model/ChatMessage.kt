package com.example.persianaiapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val isPending: Boolean = false,
    val isError: Boolean = false
) {
    companion object {
        fun createUserMessage(content: String): ChatMessage {
            return ChatMessage(
                id = UUID.randomUUID().toString(),
                content = content,
                isFromUser = true,
                timestamp = System.currentTimeMillis()
            )
        }

        fun createAIMessage(content: String): ChatMessage {
            return ChatMessage(
                id = UUID.randomUUID().toString(),
                content = content,
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            )
        }
    }
}
