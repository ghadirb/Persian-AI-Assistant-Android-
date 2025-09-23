package com.example.persianaiapp.data.local.dao

import androidx.room.*
import com.example.persianaiapp.data.local.entity.Conversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<Conversation>>
    
    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getConversationsByUser(userId: Long): Flow<List<Conversation>>
    
    @Query("SELECT * FROM conversations WHERE createdAt > :timestamp ORDER BY updatedAt DESC")
    suspend fun getConversationsAfter(timestamp: Long): List<Conversation>
    
    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: Long): Conversation?
    
    @Query("SELECT * FROM conversations WHERE isArchived = :isArchived ORDER BY updatedAt DESC")
    fun getConversationsByArchiveStatus(isArchived: Boolean): Flow<List<Conversation>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)
    
    @Update
    suspend fun updateConversation(conversation: Conversation)
    
    @Delete
    suspend fun deleteConversation(conversation: Conversation)
    
    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversationById(conversationId: Long)
    
    @Query("DELETE FROM conversations WHERE userId = :userId")
    suspend fun deleteConversationsByUser(userId: Long)
    
    @Query("DELETE FROM conversations")
    suspend fun deleteAllConversations()
    
    @Query("UPDATE conversations SET messageCount = messageCount + 1, lastMessagePreview = :preview, updatedAt = :timestamp WHERE id = :conversationId")
    suspend fun updateConversationStats(conversationId: Long, preview: String, timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM conversations WHERE isArchived = 0")
    suspend fun getActiveConversationsCount(): Int
}
