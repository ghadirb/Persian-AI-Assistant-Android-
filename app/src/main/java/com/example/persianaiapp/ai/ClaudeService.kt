package com.example.persianaiapp.ai

import com.example.persianaiapp.data.remote.dto.*
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
class ClaudeService @Inject constructor(
    private val client: OkHttpClient
) {

    suspend fun sendMessage(
        apiKey: String,
        messages: List<Message>,
        model: String = "claude-3-haiku-20240307",
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
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("anthropic-version", "2023-06-01")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val content = jsonResponse.getJSONArray("content")
                
                if (content.length() > 0) {
                    val firstContent = content.getJSONObject(0)
                    val text = firstContent.getString("text")
                    
                    val usage = jsonResponse.optJSONObject("usage")
                    val inputTokens = usage?.optInt("input_tokens") ?: 0
                    val outputTokens = usage?.optInt("output_tokens") ?: 0
                    val totalTokens = inputTokens + outputTokens
                    
                    Result.success(
                        ChatCompletionResponse(
                            content = text,
                            model = model,
                            tokensUsed = totalTokens
                        )
                    )
                } else {
                    Result.failure(Exception("No content in response"))
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
}
