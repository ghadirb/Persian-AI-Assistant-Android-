package com.example.persianaiapp.data.local.dao

import androidx.room.*
import com.example.persianaiapp.data.local.entity.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversation(conversationId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int): List<ChatMessage>

    @Query("SELECT * FROM chat_messages WHERE messageType = :messageType ORDER BY timestamp DESC")
    fun getMessagesByType(messageType: ChatMessage.MessageType): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Delete
    suspend fun deleteMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()

    @Query("SELECT * FROM chat_messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): ChatMessage?

    @Query("SELECT * FROM chat_messages WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<ChatMessage>>

    @Query("SELECT COUNT(*) FROM chat_messages WHERE isFromUser = 0 AND isProcessed = 0")
    suspend fun getUnprocessedMessagesCount(): Int
}
