package com.example.persianaiapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 1000,
    val temperature: Float = 0.7f,
    val stream: Boolean = false
)

data class Message(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class ChatCompletionResponse(
    val content: String,
    val model: String,
    @SerializedName("tokens_used")
    val tokensUsed: Int = 0,
    val finishReason: String? = null
)

data class ClaudeRequest(
    val model: String,
    val messages: List<Message>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 1000,
    val temperature: Float = 0.7f
)

data class ClaudeResponse(
    val content: List<ClaudeContent>,
    val model: String,
    val usage: ClaudeUsage?
)

data class ClaudeContent(
    val type: String,
    val text: String
)

data class ClaudeUsage(
    @SerializedName("input_tokens")
    val inputTokens: Int,
    @SerializedName("output_tokens")
    val outputTokens: Int
)

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Float = 0.7f,
    val maxOutputTokens: Int = 1000,
    val topP: Float = 0.8f,
    val topK: Int = 40
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String?
)
