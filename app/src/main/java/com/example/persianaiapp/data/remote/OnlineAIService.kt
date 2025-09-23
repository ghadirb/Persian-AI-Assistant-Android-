package com.example.persianaiapp.data.remote

import com.example.persianaiapp.data.local.entity.ChatMessage
import com.example.persianaiapp.util.Result
import com.example.persianaiapp.util.Result.Error
import com.example.persianaiapp.util.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineAIService @Inject constructor(
    private val client: OkHttpClient
) {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun getResponse(
        message: String,
        conversationHistory: List<ChatMessage> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Format the conversation history for the API
            val messages = conversationHistory.map { msg ->
                JSONObject().apply {
                    put("role", if (msg.isFromUser) "user" else "assistant")
                    put("content", msg.content)
                }
            }.toMutableList()
            
            // Add the new user message
            messages.add(JSONObject().apply {
                put("role", "user")
                put("content", message)
            })

            val requestBody = JSONObject().apply {
                put("model", "gpt-4")
                put("messages", JSONArray(messages))
                put("temperature", 0.7)
                put("max_tokens", 1000)
            }.toString()

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer ${getApiKey()}")
                .post(requestBody.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonResponse = JSONObject(responseBody)
                val content = jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                
                Success(content)
            } else {
                Error("Failed to get response from AI: ${response.message}")
            }
        } catch (e: Exception) {
            Error("Error: ${e.message}")
        }
    }

    private fun getApiKey(): String {
        // In a real app, retrieve this securely from a secure storage
        return "" // Replace with actual API key or use dependency injection
    }

    suspend fun generateText(prompt: String): Result<String> {
        // Implementation for text generation
        return getResponse(prompt)
    }

    suspend fun transcribeAudio(audioData: ByteArray): Result<String> {
        // Implementation for audio transcription
        return Error("Online transcription not implemented")
    }
}
