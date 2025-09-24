package com.example.persianaiapp.domain.repository

import com.example.persianaiapp.data.local.entity.ChatMessage
import com.example.persianaiapp.util.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatHistory(): Flow<List<ChatMessage>>
    suspend fun getResponse(message: String): Result<String>
    suspend fun clearChatHistory()
    suspend fun saveMessage(message: ChatMessage)
    suspend fun deleteMessage(messageId: Long)
}
