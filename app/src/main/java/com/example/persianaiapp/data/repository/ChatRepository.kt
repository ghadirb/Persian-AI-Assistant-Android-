package com.example.persianaiapp.data.repository

import com.example.persianaiapp.data.local.dao.ChatMessageDao
import com.example.persianaiapp.data.local.entity.ChatMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) {
    
    fun getAllMessages(): Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()
    
    fun getMessagesByConversation(conversationId: String): Flow<List<ChatMessage>> = 
        chatMessageDao.getMessagesByConversation(conversationId)
    
    suspend fun insertMessage(message: ChatMessage): Long = chatMessageDao.insertMessage(message)
    
    suspend fun updateMessage(message: ChatMessage) = chatMessageDao.updateMessage(message)
    
    suspend fun deleteMessage(message: ChatMessage) = chatMessageDao.deleteMessage(message)
    
    suspend fun deleteConversation(conversationId: String) = chatMessageDao.deleteConversation(conversationId)
    
    suspend fun getRecentMessages(limit: Int): List<ChatMessage> = chatMessageDao.getRecentMessages(limit)
    
    fun searchMessages(query: String): Flow<List<ChatMessage>> = chatMessageDao.searchMessages(query)
    
    suspend fun getUnprocessedMessagesCount(): Int = chatMessageDao.getUnprocessedMessagesCount()
    
    fun getVoiceMessages(): Flow<List<ChatMessage>> = 
        chatMessageDao.getMessagesByType(ChatMessage.MessageType.VOICE)
}
