package com.example.persianaiapp.data.remote

import com.example.persianaiapp.data.local.entity.ChatMessage
import com.example.persianaiapp.util.Result
import javax.inject.Inject
import javax.inject.Singleton

interface AIService {
    suspend fun getResponse(
        message: String,
        conversationHistory: List<ChatMessage> = emptyList()
    ): Result<String>
    
    suspend fun generateText(prompt: String): Result<String>
    suspend fun transcribeAudio(audioData: ByteArray): Result<String>
    suspend fun isModelDownloaded(modelName: String): Boolean
    suspend fun downloadModel(modelName: String, progressCallback: (Float) -> Unit): Result<Unit>
}

@Singleton
class AIServiceImpl @Inject constructor(
    private val onlineAIService: OnlineAIService,
    private val offlineAIService: OfflineAIService,
    private val settingsManager: SettingsManager
) : AIService {

    override suspend fun getResponse(
        message: String,
        conversationHistory: List<ChatMessage>
    ): Result<String> {
        return if (settingsManager.isOfflineMode()) {
            offlineAIService.getResponse(message, conversationHistory)
        } else {
            onlineAIService.getResponse(message, conversationHistory)
        }
    }

    override suspend fun generateText(prompt: String): Result<String> {
        return if (settingsManager.isOfflineMode()) {
            offlineAIService.generateText(prompt)
        } else {
            onlineAIService.generateText(prompt)
        }
    }

    override suspend fun transcribeAudio(audioData: ByteArray): Result<String> {
        return if (settingsManager.isOfflineMode()) {
            offlineAIService.transcribeAudio(audioData)
        } else {
            onlineAIService.transcribeAudio(audioData)
        }
    }

    override suspend fun isModelDownloaded(modelName: String): Boolean {
        return offlineAIService.isModelDownloaded(modelName)
    }

    override suspend fun downloadModel(modelName: String, progressCallback: (Float) -> Unit): Result<Unit> {
        return offlineAIService.downloadModel(modelName, progressCallback)
    }
}
