package com.example.persianaiapp.data.remote

import android.content.Context
import com.example.persianaiapp.util.CryptoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleDriveService @Inject constructor(
    private val context: Context,
    private val client: OkHttpClient
) {
    
    // Your Google Drive direct download link
    private val ENCRYPTED_KEYS_URL = "https://drive.google.com/uc?export=download&id=17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0"

    /**
     * Downloads and decrypts API keys from Google Drive
     */
    suspend fun downloadAndDecryptKeys(password: String): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        try {
            // Download encrypted data from Google Drive
            val request = Request.Builder()
                .url(ENCRYPTED_KEYS_URL)
                .build()

            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Failed to download keys: ${response.code}"))
            }

            val encryptedBase64 = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty response body"))

            // Decrypt the keys
            val decryptedText = CryptoUtils.decryptApiKeys(encryptedBase64.trim(), password)
                ?: return@withContext Result.failure(Exception("Failed to decrypt keys - wrong password?"))

            // Parse the decrypted text into key-value pairs
            val apiKeys = parseApiKeys(decryptedText)
            
            Result.success(apiKeys)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Parses the decrypted API keys text into a map
     * Expected format: key=value pairs, one per line
     */
    private fun parseApiKeys(decryptedText: String): Map<String, String> {
        val keys = mutableMapOf<String, String>()
        
        decryptedText.lines().forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isNotEmpty() && trimmedLine.contains("=")) {
                val parts = trimmedLine.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    keys[key] = value
                }
            }
        }
        
        return keys
    }

    /**
     * Validates that the required API keys are present
     */
    fun validateApiKeys(keys: Map<String, String>): Boolean {
        val requiredKeys = listOf(
            "OPENAI_API_KEY",
            "ANTHROPIC_API_KEY", 
            "OPENROUTER_API_KEY"
        )
        
        return requiredKeys.any { keys.containsKey(it) && keys[it]?.isNotBlank() == true }
    }

    /**
     * Gets the best available model based on available API keys
     */
    fun getBestAvailableModel(keys: Map<String, String>): String {
        return when {
            keys.containsKey("OPENAI_API_KEY") && keys["OPENAI_API_KEY"]?.isNotBlank() == true -> "gpt-4o-mini"
            keys.containsKey("ANTHROPIC_API_KEY") && keys["ANTHROPIC_API_KEY"]?.isNotBlank() == true -> "claude-3-haiku-20240307"
            keys.containsKey("OPENROUTER_API_KEY") && keys["OPENROUTER_API_KEY"]?.isNotBlank() == true -> "anthropic/claude-3-haiku"
            else -> "local"
        }
    }
}
