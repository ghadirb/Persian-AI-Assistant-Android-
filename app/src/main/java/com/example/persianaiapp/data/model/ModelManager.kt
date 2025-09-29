package com.example.persianaiapp.data.model

import android.content.Context
import android.util.Log
import com.example.persianaiapp.util.Result
import com.example.persianaiapp.util.Result.Error
import com.example.persianaiapp.util.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelManager @Inject constructor(
    private val context: Context,
    private val client: OkHttpClient
) {
    private val TAG = "ModelManager"
    private val modelsDir = File(context.filesDir, "models").apply {
        if (!exists()) mkdirs()
    }
    
    suspend fun getModelFile(modelName: String): File? = withContext(Dispatchers.IO) {
        val modelFile = File(modelsDir, modelName)
        if (modelFile.exists()) modelFile else null
    }
    
    suspend fun isModelDownloaded(modelName: String): Boolean = withContext(Dispatchers.IO) {
        File(modelsDir, modelName).exists()
    }
    
    suspend fun downloadModel(
        modelName: String,
        progressCallback: (Float) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val modelFile = File(modelsDir, modelName)
            
            // Check if model already exists
            if (modelFile.exists()) {
                return@withContext Success(Unit)
            }
            
            // Create a temporary file for downloading
            val tempFile = File(modelsDir, "$modelName.temp")
            
            // Get model URL (in a real app, this would come from a configuration)
            val modelUrl = getModelUrl(modelName)
            
            // Create request
            val request = Request.Builder()
                .url(modelUrl)
                .build()
            
            // Execute request
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@use Error("Failed to download model: ${response.code} ${response.message}")
                }
                
                val contentLength = response.body?.contentLength() ?: -1L
                var downloadedLength = 0L
                
                response.body?.let { body ->
                    body.byteStream().use { inputStream ->
                        tempFile.sink().buffer().use { sink ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            var bytesRead: Int
                            
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                sink.write(buffer, 0, bytesRead)
                                downloadedLength += bytesRead
                                
                                // Calculate and report progress
                                if (contentLength > 0) {
                                    val progress = (downloadedLength * 100 / contentLength).toFloat()
                                    progressCallback(progress)
                                }
                            }
                            
                            sink.flush()
                        }
                    }
                }
                
                // Rename temp file to final name
                if (!tempFile.renameTo(modelFile)) {
                    return@use Error("Failed to save model file")
                }
                
                Success(Unit)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error downloading model", e)
            Error("Error downloading model: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            Error("Unexpected error: ${e.message}")
        }
    }
    
    suspend fun deleteModel(modelName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val modelFile = File(modelsDir, modelName)
            if (modelFile.exists()) {
                return@withContext modelFile.delete()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting model", e)
            false
        }
    }
    
    suspend fun getAvailableModels(): List<AIModel> = withContext(Dispatchers.IO) {
        // In a real app, this would fetch available models from a server
        listOf(
            AIModel(
                id = "chat_model.tflite",
                name = "Persian Chat Model",
                description = "Lightweight model for chat in Persian",
                sizeMB = 120,
                isDownloaded = isModelDownloaded("chat_model.tflite")
            ),
            AIModel(
                id = "whisper.tflite",
                name = "Speech Recognition",
                description = "Model for speech-to-text in Persian",
                sizeMB = 150,
                isDownloaded = isModelDownloaded("whisper.tflite")
            )
        )
    }
    
    private fun getModelUrl(modelName: String): String {
        // In a real app, this would return the appropriate URL for the model
        return "https://example.com/models/$modelName"
    }
    
    data class AIModel(
        val id: String,
        val name: String,
        val description: String,
        val sizeMB: Int,
        val isDownloaded: Boolean
    )
    
    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }
}
