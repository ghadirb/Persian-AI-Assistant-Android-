package com.example.persianaiapp.data.backup

import android.content.Context
import com.example.persianaiapp.data.local.dao.ConversationDao
import com.example.persianaiapp.data.local.dao.MessageDao
import com.example.persianaiapp.data.local.entity.Conversation
import com.example.persianaiapp.data.local.entity.Message
import com.example.persianaiapp.security.EncryptionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationMemoryManager @Inject constructor(
    private val context: Context,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val encryptionManager: EncryptionManager,
    private val gson: Gson
) {

    data class ConversationMemory(
        val conversations: List<Conversation>,
        val messages: List<Message>,
        val exportDate: String,
        val totalConversations: Int,
        val totalMessages: Int
    )

    suspend fun createMemoryBackup(includeAllConversations: Boolean = true): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                val conversations = if (includeAllConversations) {
                    conversationDao.getAllConversations()
                } else {
                    // Only backup conversations from last 30 days
                    val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
                    conversationDao.getConversationsAfter(thirtyDaysAgo)
                }

                val allMessages = mutableListOf<Message>()
                conversations.forEach { conversation ->
                    val messages = messageDao.getMessagesForConversation(conversation.id)
                    allMessages.addAll(messages)
                }

                val memory = ConversationMemory(
                    conversations = conversations,
                    messages = allMessages,
                    exportDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    totalConversations = conversations.size,
                    totalMessages = allMessages.size
                )

                val memoryJson = gson.toJson(memory)
                val encryptedData = encryptionManager.encrypt(memoryJson.toByteArray())

                val backupDir = File(context.getExternalFilesDir(null), "memory_backups")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }

                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val backupFile = File(backupDir, "conversation_memory_$timestamp.enc")
                
                backupFile.writeBytes(encryptedData)

                Timber.d("Memory backup created: ${backupFile.absolutePath}")
                Timber.d("Backed up ${conversations.size} conversations and ${allMessages.size} messages")

                Result.success(backupFile)
            } catch (e: Exception) {
                Timber.e(e, "Error creating memory backup")
                Result.failure(e)
            }
        }
    }

    suspend fun restoreMemoryBackup(backupFile: File, mergeWithExisting: Boolean = true): Result<ConversationMemory> {
        return withContext(Dispatchers.IO) {
            try {
                val encryptedData = backupFile.readBytes()
                val decryptedData = encryptionManager.decrypt(encryptedData)
                val memoryJson = String(decryptedData, Charsets.UTF_8)

                val type = object : TypeToken<ConversationMemory>() {}.type
                val memory: ConversationMemory = gson.fromJson(memoryJson, type)

                if (!mergeWithExisting) {
                    // Clear existing data
                    messageDao.deleteAllMessages()
                    conversationDao.deleteAllConversations()
                }

                // Restore conversations
                memory.conversations.forEach { conversation ->
                    try {
                        if (mergeWithExisting) {
                            // Check if conversation already exists
                            val existing = conversationDao.getConversationById(conversation.id)
                            if (existing == null) {
                                conversationDao.insertConversation(conversation)
                            }
                        } else {
                            conversationDao.insertConversation(conversation)
                        }
                    } catch (e: Exception) {
                        Timber.w(e, "Error inserting conversation ${conversation.id}")
                    }
                }

                // Restore messages
                memory.messages.forEach { message ->
                    try {
                        if (mergeWithExisting) {
                            // Check if message already exists
                            val existing = messageDao.getMessageById(message.id)
                            if (existing == null) {
                                messageDao.insertMessage(message)
                            }
                        } else {
                            messageDao.insertMessage(message)
                        }
                    } catch (e: Exception) {
                        Timber.w(e, "Error inserting message ${message.id}")
                    }
                }

                Timber.d("Memory backup restored: ${memory.totalConversations} conversations, ${memory.totalMessages} messages")
                Result.success(memory)
            } catch (e: Exception) {
                Timber.e(e, "Error restoring memory backup")
                Result.failure(e)
            }
        }
    }

    suspend fun getAvailableBackups(): List<File> {
        return withContext(Dispatchers.IO) {
            try {
                val backupDir = File(context.getExternalFilesDir(null), "memory_backups")
                if (!backupDir.exists()) {
                    return@withContext emptyList()
                }

                backupDir.listFiles { file ->
                    file.isFile && file.name.startsWith("conversation_memory_") && file.name.endsWith(".enc")
                }?.sortedByDescending { it.lastModified() }?.toList() ?: emptyList()
            } catch (e: Exception) {
                Timber.e(e, "Error getting available backups")
                emptyList()
            }
        }
    }

    suspend fun deleteBackup(backupFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                backupFile.delete()
            } catch (e: Exception) {
                Timber.e(e, "Error deleting backup file")
                false
            }
        }
    }

    suspend fun getMemoryStatistics(): MemoryStatistics {
        return withContext(Dispatchers.IO) {
            try {
                val totalConversations = conversationDao.getConversationCount()
                val totalMessages = messageDao.getMessageCount()
                val oldestConversation = conversationDao.getOldestConversation()
                val newestConversation = conversationDao.getNewestConversation()

                MemoryStatistics(
                    totalConversations = totalConversations,
                    totalMessages = totalMessages,
                    oldestConversationDate = oldestConversation?.createdAt,
                    newestConversationDate = newestConversation?.createdAt,
                    averageMessagesPerConversation = if (totalConversations > 0) totalMessages.toDouble() / totalConversations else 0.0
                )
            } catch (e: Exception) {
                Timber.e(e, "Error getting memory statistics")
                MemoryStatistics()
            }
        }
    }

    data class MemoryStatistics(
        val totalConversations: Int = 0,
        val totalMessages: Int = 0,
        val oldestConversationDate: Long? = null,
        val newestConversationDate: Long? = null,
        val averageMessagesPerConversation: Double = 0.0
    )

    suspend fun cleanupOldConversations(daysToKeep: Int = 90): Int {
        return withContext(Dispatchers.IO) {
            try {
                val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
                val deletedCount = conversationDao.deleteConversationsOlderThan(cutoffTime)
                
                // Also cleanup orphaned messages
                messageDao.deleteOrphanedMessages()
                
                Timber.d("Cleaned up $deletedCount old conversations (older than $daysToKeep days)")
                deletedCount
            } catch (e: Exception) {
                Timber.e(e, "Error cleaning up old conversations")
                0
            }
        }
    }

    suspend fun exportConversationToText(conversationId: Long): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val conversation = conversationDao.getConversationById(conversationId)
                    ?: return@withContext Result.failure(Exception("Conversation not found"))

                val messages = messageDao.getMessagesForConversation(conversationId)
                    .sortedBy { it.timestamp }

                val sb = StringBuilder()
                sb.appendLine("گفتگو: ${conversation.title}")
                sb.appendLine("تاریخ ایجاد: ${SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(conversation.createdAt))}")
                sb.appendLine("تعداد پیام‌ها: ${messages.size}")
                sb.appendLine("=" * 50)
                sb.appendLine()

                messages.forEach { message ->
                    val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                    val sender = if (message.isFromUser) "کاربر" else "دستیار"
                    sb.appendLine("[$timestamp] $sender:")
                    sb.appendLine(message.content)
                    sb.appendLine()
                }

                Result.success(sb.toString())
            } catch (e: Exception) {
                Timber.e(e, "Error exporting conversation to text")
                Result.failure(e)
            }
        }
    }
}
