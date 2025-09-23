package com.example.persianaiapp.data.repository

import com.example.persianaiapp.data.local.dao.MemoryDao
import com.example.persianaiapp.data.local.entity.Memory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val memoryDao: MemoryDao
) {
    
    fun getAllMemories(): Flow<List<Memory>> = memoryDao.getAllMemories()
    
    fun getRecentMemories(limit: Int): Flow<List<Memory>> = memoryDao.getRecentMemories(limit)
    
    fun getMemoriesByCategory(category: Memory.MemoryCategory): Flow<List<Memory>> = 
        memoryDao.getMemoriesByCategory(category)
    
    fun getMemoriesByPriority(priority: Memory.MemoryPriority): Flow<List<Memory>> = 
        memoryDao.getMemoriesByPriority(priority)
    
    fun getUpcomingReminders(): Flow<List<Memory>> = 
        memoryDao.getUpcomingReminders(System.currentTimeMillis())
    
    suspend fun insertMemory(memory: Memory): Long = memoryDao.insertMemory(memory)
    
    suspend fun updateMemory(memory: Memory) = memoryDao.updateMemory(memory)
    
    suspend fun deleteMemory(memory: Memory) = memoryDao.deleteMemory(memory)
    
    suspend fun archiveMemory(id: Long) = memoryDao.updateArchiveStatus(id, true)
    
    suspend fun unarchiveMemory(id: Long) = memoryDao.updateArchiveStatus(id, false)
    
    fun searchMemories(query: String): Flow<List<Memory>> = memoryDao.searchMemories(query)
    
    fun getMemoriesByTag(tag: String): Flow<List<Memory>> = memoryDao.getMemoriesByTag(tag)
    
    suspend fun getActiveMemoriesCount(): Int = memoryDao.getActiveMemoriesCount()
}
