package com.example.persianaiapp.ai

import com.example.persianaiapp.data.remote.dto.ChatCompletionRequest
import com.example.persianaiapp.data.remote.dto.ChatCompletionResponse
import com.example.persianaiapp.data.remote.dto.Message
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
class OpenAIService @Inject constructor(
    private val client: OkHttpClient
) {

    suspend fun sendChatCompletion(
        apiKey: String,
        messages: List<Message>,
        model: String = "gpt-4o-mini",
        maxTokens: Int = 1000,
        temperature: Float = 0.7f
    ): Result<ChatCompletionResponse> = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("model", model)
                put("max_tokens", maxTokens)
                put("temperature", temperature)
                put("messages", JSONArray().apply {
                    messages.forEach { message ->
                        put(JSONObject().apply {
                            put("role", message.role)
                            put("content", message.content)
                        })
                    }
                })
            }

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val choices = jsonResponse.getJSONArray("choices")
                
                if (choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.getJSONObject("message")
                    val content = message.getString("content")
                    
                    val usage = jsonResponse.optJSONObject("usage")
                    val totalTokens = usage?.optInt("total_tokens") ?: 0
                    
                    Result.success(
                        ChatCompletionResponse(
                            content = content,
                            model = model,
                            tokensUsed = totalTokens
                        )
                    )
                } else {
                    Result.failure(Exception("No response choices available"))
                }
            } else {
                val errorMessage = if (responseBody != null) {
                    try {
                        val errorJson = JSONObject(responseBody)
                        errorJson.optJSONObject("error")?.optString("message") ?: "Unknown API error"
                    } catch (e: Exception) {
                        "HTTP ${response.code}: ${response.message}"
                    }
                } else {
                    "HTTP ${response.code}: ${response.message}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateSpeech(
        apiKey: String,
        text: String,
        voice: String = "alloy",
        model: String = "tts-1"
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("model", model)
                put("input", text)
                put("voice", voice)
            }

            val request = Request.Builder()
                .url("https://api.openai.com/v1/audio/speech")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val audioData = response.body?.bytes()
                if (audioData != null) {
                    Result.success(audioData)
                } else {
                    Result.failure(Exception("No audio data received"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun transcribeAudio(
        apiKey: String,
        audioData: ByteArray,
        language: String = "fa"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Note: This would require multipart form data implementation
            // For now, returning a placeholder
            Result.success("Transcription placeholder - implement multipart upload")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
