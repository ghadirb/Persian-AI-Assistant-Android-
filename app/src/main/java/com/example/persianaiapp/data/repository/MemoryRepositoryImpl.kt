package com.example.persianaiapp.data.repository

import com.example.persianaiapp.data.local.dao.MemoryDao
import com.example.persianaiapp.domain.model.Memory
import com.example.persianaiapp.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemoryRepositoryImpl @Inject constructor(
    private val dao: MemoryDao
) : MemoryRepository {

    override fun getAllMemories(): Flow<List<Memory>> = dao.getAllMemories()

    override suspend fun insertMemory(memory: Memory) = dao.insertMemory(memory)

    override suspend fun deleteMemory(memory: Memory) = dao.deleteMemory(memory)

    override suspend fun updateMemory(memory: Memory) = dao.updateMemory(memory)

    override fun searchMemories(query: String): Flow<List<Memory>> = dao.searchMemories(query)

    override suspend fun pinMemory(id: Int, isPinned: Boolean) = dao.pinMemory(id, isPinned)
}
