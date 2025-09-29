package com.example.persianaiapp.domain.repository

import com.example.persianaiapp.domain.model.Memory
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {
    fun getAllMemories(): Flow<List<Memory>>
    suspend fun insertMemory(memory: Memory)
    suspend fun deleteMemory(memory: Memory)
    suspend fun updateMemory(memory: Memory)
    fun searchMemories(query: String): Flow<List<Memory>>
    suspend fun pinMemory(id: Int, isPinned: Boolean)
}
