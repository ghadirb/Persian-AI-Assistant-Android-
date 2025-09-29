package com.example.persianaiapp.data.local.dao

import androidx.room.*
import com.example.persianaiapp.data.local.entity.Memory
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memories WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllMemories(): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE isArchived = 0 ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentMemories(limit: Int): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE category = :category AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getMemoriesByCategory(category: Memory.MemoryCategory): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE priority = :priority AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getMemoriesByPriority(priority: Memory.MemoryPriority): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE reminderTime IS NOT NULL AND reminderTime > :currentTime ORDER BY reminderTime ASC")
    fun getUpcomingReminders(currentTime: Long): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemoryById(id: Long): Memory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: Memory): Long

    @Update
    suspend fun updateMemory(memory: Memory)

    @Delete
    suspend fun deleteMemory(memory: Memory)

    @Query("UPDATE memories SET isArchived = :isArchived WHERE id = :id")
    suspend fun updateArchiveStatus(id: Long, isArchived: Boolean)

    @Query("SELECT * FROM memories WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchMemories(query: String): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE tags LIKE '%' || :tag || '%'")
    fun getMemoriesByTag(tag: String): Flow<List<Memory>>

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("SELECT COUNT(*) FROM memories WHERE isArchived = 0")
    suspend fun getActiveMemoriesCount(): Int
}
