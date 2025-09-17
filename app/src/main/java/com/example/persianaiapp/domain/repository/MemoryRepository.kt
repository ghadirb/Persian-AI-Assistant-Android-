package com.example.persianaiapp.domain.repository

import com.example.persianaiapp.data.local.entity.Memory
import com.example.persianaiapp.util.Result
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MemoryRepository {
    fun getAllMemories(): Flow<List<Memory>>
    fun getRecentMemories(limit: Int): Flow<List<Memory>>
    suspend fun getMemoryById(id: Long): Result<Memory>
    suspend fun saveMemory(memory: Memory): Result<Long>
    suspend fun updateMemory(memory: Memory): Result<Unit>
    suspend fun deleteMemory(memory: Memory): Result<Unit>
    suspend fun togglePinMemory(id: Long, isPinned: Boolean): Result<Unit>
    suspend fun archiveMemory(id: Long, isArchived: Boolean): Result<Unit>
    fun searchMemories(query: String): Flow<List<Memory>>
    fun getMemoriesByTag(tag: String): Flow<List<Memory>>
}
