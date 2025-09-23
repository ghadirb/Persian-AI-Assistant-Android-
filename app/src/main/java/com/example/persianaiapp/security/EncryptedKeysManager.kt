package com.example.persianaiapp.security

import android.content.Context
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.SecretKeyFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedKeysManager @Inject constructor(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    companion object {
        private const val GOOGLE_DRIVE_KEYS_URL = "https://drive.google.com/uc?export=download&id=17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0"
        private const val DEFAULT_PASSWORD = "12345"
        private const val PBKDF2_ITERATIONS = 20000
        private const val AES_KEY_LENGTH = 32
        private const val GCM_IV_LENGTH = 12
        private const val SALT_LENGTH = 16
    }

    private var cachedKeys: Map<String, String>? = null
    private var isKeysValid = false

    suspend fun downloadAndDecryptKeys(password: String = DEFAULT_PASSWORD): Result<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                // Download encrypted keys from Google Drive
                val encryptedData = downloadKeysFromGoogleDrive()
                if (encryptedData.isEmpty()) {
                    return@withContext Result.failure(Exception("Failed to download keys from Google Drive"))
                }

                // Decrypt the keys
                val decryptedKeys = decryptKeys(encryptedData, password)
                if (decryptedKeys.isNotEmpty()) {
                    cachedKeys = decryptedKeys
                    isKeysValid = true
                    Timber.d("Successfully decrypted ${decryptedKeys.size} API keys")
                    Result.success(decryptedKeys)
                } else {
                    isKeysValid = false
                    Result.failure(Exception("Failed to decrypt keys - invalid password or corrupted data"))
                }
            } catch (e: Exception) {
                isKeysValid = false
                Timber.e(e, "Error downloading or decrypting keys")
                Result.failure(e)
            }
        }
    }

    private suspend fun downloadKeysFromGoogleDrive(): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(GOOGLE_DRIVE_KEYS_URL)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val base64Data = response.body?.string() ?: ""
                    Base64.decode(base64Data, Base64.DEFAULT)
                } else {
                    Timber.e("Failed to download keys: ${response.code}")
                    byteArrayOf()
                }
            } catch (e: IOException) {
                Timber.e(e, "Network error downloading keys")
                byteArrayOf()
            }
        }
    }

    private fun decryptKeys(encryptedData: ByteArray, password: String): Map<String, String> {
        return try {
            if (encryptedData.size < SALT_LENGTH + GCM_IV_LENGTH) {
                Timber.e("Encrypted data too short")
                return emptyMap()
            }

            // Extract salt, IV, and ciphertext
            val salt = encryptedData.sliceArray(0 until SALT_LENGTH)
            val iv = encryptedData.sliceArray(SALT_LENGTH until SALT_LENGTH + GCM_IV_LENGTH)
            val ciphertext = encryptedData.sliceArray(SALT_LENGTH + GCM_IV_LENGTH until encryptedData.size)

            // Derive key using PBKDF2
            val keySpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, AES_KEY_LENGTH * 8)
            val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val key = keyFactory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(key, "AES")

            // Decrypt using AES-GCM
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            val decryptedBytes = cipher.doFinal(ciphertext)

            // Parse decrypted JSON-like format
            val decryptedText = String(decryptedBytes, Charsets.UTF_8)
            parseKeysFromText(decryptedText)
        } catch (e: Exception) {
            Timber.e(e, "Error decrypting keys")
            emptyMap()
        }
    }

    private fun parseKeysFromText(text: String): Map<String, String> {
        val keys = mutableMapOf<String, String>()
        try {
            // Parse simple key=value format
            text.lines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && trimmed.contains("=")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim()
                        keys[key] = value
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error parsing keys")
        }
        return keys
    }

    fun getApiKey(provider: String): String? {
        return cachedKeys?.get("${provider.uppercase()}_API_KEY")
    }

    fun areKeysValid(): Boolean = isKeysValid

    fun getCachedKeys(): Map<String, String>? = cachedKeys

    fun clearKeys() {
        cachedKeys = null
        isKeysValid = false
    }

    // Test key validity by making a simple API call
    suspend fun validateApiKey(provider: String, apiKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                when (provider.lowercase()) {
                    "openai" -> validateOpenAIKey(apiKey)
                    "claude" -> validateClaudeKey(apiKey)
                    "gemini" -> validateGeminiKey(apiKey)
                    else -> false
                }
            } catch (e: Exception) {
                Timber.e(e, "Error validating $provider API key")
                false
            }
        }
    }

    private suspend fun validateOpenAIKey(apiKey: String): Boolean {
        return try {
            val request = Request.Builder()
                .url("https://api.openai.com/v1/models")
                .header("Authorization", "Bearer $apiKey")
                .build()

            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun validateClaudeKey(apiKey: String): Boolean {
        return try {
            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .build()

            val response = okHttpClient.newCall(request).execute()
            response.code != 401 // Not unauthorized
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun validateGeminiKey(apiKey: String): Boolean {
        return try {
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey")
                .build()

            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
