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
class GeminiService @Inject constructor(
    private val client: OkHttpClient
) {

    suspend fun generateContent(
        apiKey: String,
        messages: List<Message>,
        model: String = "gemini-1.5-flash",
        maxTokens: Int = 1000,
        temperature: Float = 0.7f
    ): Result<ChatCompletionResponse> = withContext(Dispatchers.IO) {
        try {
            val contents = JSONArray().apply {
                messages.forEach { message ->
                    put(JSONObject().apply {
                        put("role", if (message.role == "assistant") "model" else message.role)
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", message.content)
                            })
                        })
                    })
                }
            }

            val requestBody = JSONObject().apply {
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", temperature)
                    put("maxOutputTokens", maxTokens)
                    put("topP", 0.8)
                    put("topK", 40)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.getJSONArray("candidates")
                
                if (candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    
                    if (parts.length() > 0) {
                        val text = parts.getJSONObject(0).getString("text")
                        
                        Result.success(
                            ChatCompletionResponse(
                                content = text,
                                model = model,
                                tokensUsed = 0 // Gemini doesn't return token usage in free tier
                            )
                        )
                    } else {
                        Result.failure(Exception("No parts in response"))
                    }
                } else {
                    Result.failure(Exception("No candidates in response"))
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
