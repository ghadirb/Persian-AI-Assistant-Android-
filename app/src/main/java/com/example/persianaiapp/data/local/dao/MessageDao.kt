package com.example.persianaiapp.data.local.dao

import androidx.room.*
import com.example.persianaiapp.data.local.entity.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesForConversationSync(conversationId: Long): List<Message>
    
    @Query("SELECT * FROM messages WHERE conversationId IN (:conversationIds) ORDER BY timestamp DESC")
    suspend fun getMessagesForConversations(conversationIds: List<Long>): List<Message>
    
    @Query("SELECT * FROM messages WHERE isFromUser = :isFromUser ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessagesByUser(isFromUser: Boolean, limit: Int): List<Message>
    
    @Query("SELECT * FROM messages WHERE messageType = :messageType ORDER BY timestamp DESC")
    fun getMessagesByType(messageType: String): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND content LIKE '%' || :query || '%' ORDER BY timestamp ASC")
    fun searchMessagesInConversation(conversationId: Long, query: String): Flow<List<Message>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    @Update
    suspend fun updateMessage(message: Message)
    
    @Delete
    suspend fun deleteMessage(message: Message)
    
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)
    
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: Long)
    
    @Query("DELETE FROM messages WHERE conversationId IN (:conversationIds)")
    suspend fun deleteMessagesForConversations(conversationIds: List<Long>)
    
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
    
    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): Message?
    
    @Query("SELECT COUNT(*) FROM messages WHERE isFromUser = 0 AND isProcessed = 0")
    suspend fun getUnprocessedMessagesCount(): Int
    
    @Query("UPDATE messages SET isProcessed = 1 WHERE id = :messageId")
    suspend fun markMessageAsProcessed(messageId: Long)
    
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    suspend fun getMessageCountForConversation(conversationId: Long): Int
}
