package com.example.persianaiapp.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.SecretKeyFactory

object CryptoUtils {
    
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_LENGTH = 256
    private const val IV_LENGTH = 12
    private const val SALT_LENGTH = 16
    private const val TAG_LENGTH = 16
    private const val ITERATION_COUNT = 20000

    /**
     * Decrypts the encrypted API keys using the provided password
     * This matches the encryption format from your Python script
     */
    fun decryptApiKeys(encryptedBase64: String, password: String): String? {
        return try {
            // Decode base64
            val encryptedData = Base64.decode(encryptedBase64, Base64.DEFAULT)
            
            // Extract salt (first 16 bytes)
            val salt = encryptedData.sliceArray(0..15)
            
            // Extract IV (next 12 bytes)
            val iv = encryptedData.sliceArray(16..27)
            
            // Extract ciphertext (remaining bytes)
            val ciphertext = encryptedData.sliceArray(28 until encryptedData.size)
            
            // Derive key using PBKDF2
            val keySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val key = keyFactory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(key, ALGORITHM)
            
            // Decrypt
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            
            val decryptedBytes = cipher.doFinal(ciphertext)
            String(decryptedBytes, Charsets.UTF_8)
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Encrypts data using AES-GCM with PBKDF2 key derivation
     */
    fun encryptData(plaintext: String, password: String): String? {
        return try {
            val salt = ByteArray(SALT_LENGTH)
            val iv = ByteArray(IV_LENGTH)
            SecureRandom().nextBytes(salt)
            SecureRandom().nextBytes(iv)
            
            // Derive key using PBKDF2
            val keySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val key = keyFactory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(key, ALGORITHM)
            
            // Encrypt
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH * 8, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
            
            val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            
            // Combine salt + iv + ciphertext
            val combined = salt + iv + ciphertext
            
            // Encode to base64
            Base64.encodeToString(combined, Base64.DEFAULT)
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generates a secure random password
     */
    fun generateSecurePassword(length: Int = 16): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        val random = SecureRandom()
        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    /**
     * Hashes a password using PBKDF2
     */
    fun hashPassword(password: String, salt: ByteArray = ByteArray(16).apply { SecureRandom().nextBytes(this) }): Pair<String, ByteArray> {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = keyFactory.generateSecret(keySpec).encoded
        return Base64.encodeToString(hash, Base64.DEFAULT) to salt
    }

    /**
     * Verifies a password against a hash
     */
    fun verifyPassword(password: String, hash: String, salt: ByteArray): Boolean {
        val (newHash, _) = hashPassword(password, salt)
        return newHash == hash
    }
}
