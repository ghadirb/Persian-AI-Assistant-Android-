package com.example.persianaiapp.data.repository

import com.example.persianaiapp.data.local.dao.ChatMessageDao
import com.example.persianaiapp.data.local.entity.ChatMessage
import com.example.persianaiapp.data.remote.AIService
import com.example.persianaiapp.domain.repository.ChatRepository
import com.example.persianaiapp.util.Result
import com.example.persianaiapp.util.Result.Error
import com.example.persianaiapp.util.Result.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val aiService: AIService
) : ChatRepository {

    override fun getChatHistory(): Flow<List<ChatMessage>> {
        return chatMessageDao.getAllMessages()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getResponse(userMessage: String): Result<String> {
        return try {
            // Get the conversation history
            val conversationHistory = withContext(Dispatchers.IO) {
                chatMessageDao.getRecentMessages(10) // Get last 10 messages for context
            }

            // Get AI response
            val response = aiService.getResponse(userMessage, conversationHistory)
            
            // Save the AI response to the database
            if (response is Success) {
                val aiMessage = ChatMessage(
                    content = response.data,
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )
                saveMessage(aiMessage)
            }
            
            response
        } catch (e: Exception) {
            Error(e.message ?: "Error getting response from AI")
        }
    }

    override suspend fun clearChatHistory() {
        withContext(Dispatchers.IO) {
            chatMessageDao.deleteAllMessages()
        }
    }

    override suspend fun saveMessage(message: ChatMessage) {
        withContext(Dispatchers.IO) {
            chatMessageDao.insertMessage(message)
        }
    }

    override suspend fun deleteMessage(messageId: Long) {
        withContext(Dispatchers.IO) {
            chatMessageDao.deleteMessageById(messageId)
        }
    }
}
