package com.example.persianaiapp.data.repository

import com.example.persianaiapp.data.local.dao.MemoryDao
import com.example.persianaiapp.data.local.entity.Memory
import com.example.persianaiapp.domain.repository.MemoryRepository
import com.example.persianaiapp.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao
) : MemoryRepository {

    override fun getAllMemories(): Flow<List<Memory>> {
        return memoryDao.getAllMemories().flowOn(Dispatchers.IO)
    }

    override fun getRecentMemories(limit: Int): Flow<List<Memory>> {
        return memoryDao.getRecentMemories(limit).flowOn(Dispatchers.IO)
    }

    override suspend fun getMemoryById(id: Long): Result<Memory> {
        return try {
            val memory = withContext(Dispatchers.IO) {
                memoryDao.getMemoryById(id)
            }
            if (memory != null) {
                Result.Success(memory)
            } else {
                Result.Error("Memory not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun saveMemory(memory: Memory): Result<Long> {
        return try {
            val id = withContext(Dispatchers.IO) {
                memoryDao.insertMemory(memory)
            }
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save memory")
        }
    }

    override suspend fun updateMemory(memory: Memory): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                memoryDao.updateMemory(memory)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update memory")
        }
    }

    override suspend fun deleteMemory(memory: Memory): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                memoryDao.deleteMemory(memory)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete memory")
        }
    }

    override suspend fun togglePinMemory(id: Long, isPinned: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                memoryDao.updatePinnedStatus(id, isPinned)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to toggle pin status")
        }
    }

    override suspend fun archiveMemory(id: Long, isArchived: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                memoryDao.updateArchiveStatus(id, isArchived)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update archive status")
        }
    }

    override fun searchMemories(query: String): Flow<List<Memory>> {
        return memoryDao.searchMemories(query).flowOn(Dispatchers.IO)
    }

    override fun getMemoriesByTag(tag: String): Flow<List<Memory>> {
        return memoryDao.getMemoriesByTag(tag).flowOn(Dispatchers.IO)
    }
}
