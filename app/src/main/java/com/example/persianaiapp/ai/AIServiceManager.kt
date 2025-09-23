package com.example.persianaiapp.ai

import com.example.persianaiapp.data.remote.dto.ChatCompletionResponse
import com.example.persianaiapp.data.remote.dto.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIServiceManager @Inject constructor(
    private val openAIService: OpenAIService,
    private val claudeService: ClaudeService,
    private val geminiService: GeminiService
) {

    suspend fun sendMessage(
        provider: String,
        apiKey: String,
        messages: List<Message>,
        model: String,
        maxTokens: Int = 1000,
        temperature: Float = 0.7f
    ): Result<ChatCompletionResponse> {
        return when (provider.lowercase()) {
            "openai" -> openAIService.sendChatCompletion(
                apiKey = apiKey,
                messages = messages,
                model = model,
                maxTokens = maxTokens,
                temperature = temperature
            )
            "claude", "anthropic" -> claudeService.sendMessage(
                apiKey = apiKey,
                messages = messages,
                model = model,
                maxTokens = maxTokens,
                temperature = temperature
            )
            "gemini", "google" -> geminiService.generateContent(
                apiKey = apiKey,
                messages = messages,
                model = model,
                maxTokens = maxTokens,
                temperature = temperature
            )
            else -> Result.failure(Exception("Unsupported AI provider: $provider"))
        }
    }

    suspend fun generateSpeech(
        apiKey: String,
        text: String,
        voice: String = "alloy"
    ): Result<ByteArray> {
        return openAIService.generateSpeech(apiKey, text, voice)
    }

    suspend fun transcribeAudio(
        apiKey: String,
        audioData: ByteArray,
        language: String = "fa"
    ): Result<String> {
        return openAIService.transcribeAudio(apiKey, audioData, language)
    }

    fun getSupportedProviders(): List<String> {
        return listOf("openai", "claude", "gemini")
    }

    fun getModelsForProvider(provider: String): List<String> {
        return when (provider.lowercase()) {
            "openai" -> listOf(
                "gpt-4o",
                "gpt-4o-mini", 
                "gpt-4-turbo",
                "gpt-3.5-turbo"
            )
            "claude", "anthropic" -> listOf(
                "claude-3-opus-20240229",
                "claude-3-sonnet-20240229",
                "claude-3-haiku-20240307"
            )
            "gemini", "google" -> listOf(
                "gemini-1.5-pro",
                "gemini-1.5-flash",
                "gemini-pro"
            )
            else -> emptyList()
        }
    }
}
