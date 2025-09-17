package com.example.persianaiapp.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class LocalModelManager @Inject constructor(
    private val context: Context,
    private val client: OkHttpClient
) {
    
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private val availableModels = listOf(
        ModelInfo(
            name = "Llama-2-7B-Chat-GGML",
            displayName = "Llama 2 Chat 7B",
            sizeGB = 3.5f,
            downloadUrl = "https://huggingface.co/TheBloke/Llama-2-7B-Chat-GGML/resolve/main/llama-2-7b-chat.q4_0.bin",
            description = "مدل چت عمومی با کیفیت بالا"
        ),
        ModelInfo(
            name = "Mistral-7B-Instruct-GGUF",
            displayName = "Mistral 7B Instruct",
            sizeGB = 4.1f,
            downloadUrl = "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.1-GGUF/resolve/main/mistral-7b-instruct-v0.1.q4_0.gguf",
            description = "مدل دستورالعمل با عملکرد سریع"
        ),
        ModelInfo(
            name = "Persian-Llama-7B-GGUF",
            displayName = "Persian Llama 7B",
            sizeGB = 4.0f,
            downloadUrl = "https://huggingface.co/persian-llm/Persian-Llama-7B-GGUF/resolve/main/persian-llama-7b.q4_0.gguf",
            description = "مدل فارسی بهینه‌شده"
        ),
        ModelInfo(
            name = "CodeLlama-7B-Instruct-GGUF",
            displayName = "Code Llama 7B",
            sizeGB = 3.8f,
            downloadUrl = "https://huggingface.co/TheBloke/CodeLlama-7B-Instruct-GGUF/resolve/main/codellama-7b-instruct.q4_0.gguf",
            description = "مدل تخصصی برای کدنویسی"
        )
    )

    fun getAvailableModels(): List<ModelInfo> = availableModels

    fun getInstalledModels(): List<ModelInfo> {
        val modelsDir = getModelsDirectory()
        return availableModels.filter { model ->
            val modelFile = File(modelsDir, "${model.name}.gguf")
            modelFile.exists() && modelFile.length() > 0
        }
    }

    suspend fun downloadModel(modelName: String): Result<File> = withContext(Dispatchers.IO) {
        val model = availableModels.find { it.name == modelName }
            ?: return@withContext Result.failure(Exception("مدل یافت نشد"))

        try {
            _downloadState.value = DownloadState.Downloading(modelName, 0f)

            val modelsDir = getModelsDirectory()
            val modelFile = File(modelsDir, "${model.name}.gguf")

            // Check if already exists
            if (modelFile.exists() && modelFile.length() > 0) {
                _downloadState.value = DownloadState.Completed(modelName)
                return@withContext Result.success(modelFile)
            }

            val request = Request.Builder()
                .url(model.downloadUrl)
                .build()

            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                _downloadState.value = DownloadState.Error(modelName, "خطا در دانلود: ${response.code}")
                return@withContext Result.failure(IOException("HTTP ${response.code}"))
            }

            val responseBody = response.body ?: throw IOException("پاسخ خالی")
            val contentLength = responseBody.contentLength()

            val sink = modelFile.sink().buffer()
            val source = responseBody.source()

            var totalBytesRead = 0L
            val bufferSize = 8192L

            while (true) {
                val bytesRead = source.read(sink.buffer, bufferSize)
                if (bytesRead == -1L) break

                totalBytesRead += bytesRead
                sink.emit()

                if (contentLength > 0) {
                    val progress = (totalBytesRead.toFloat() / contentLength.toFloat())
                    _downloadState.value = DownloadState.Downloading(modelName, progress)
                }
            }

            sink.close()
            source.close()

            _downloadState.value = DownloadState.Completed(modelName)
            Result.success(modelFile)

        } catch (e: Exception) {
            _downloadState.value = DownloadState.Error(modelName, e.message ?: "خطای نامشخص")
            Result.failure(e)
        }
    }

    suspend fun deleteModel(modelName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val modelsDir = getModelsDirectory()
            val modelFile = File(modelsDir, "${modelName}.gguf")
            
            if (modelFile.exists()) {
                modelFile.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getModelPath(modelName: String): String? {
        val modelsDir = getModelsDirectory()
        val modelFile = File(modelsDir, "${modelName}.gguf")
        
        return if (modelFile.exists() && modelFile.length() > 0) {
            modelFile.absolutePath
        } else {
            null
        }
    }

    fun validateModelFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.exists() && file.length() > 0 && (
                file.extension.lowercase() in listOf("gguf", "ggml", "bin")
            )
        } catch (e: Exception) {
            false
        }
    }

    fun importModel(sourceFile: File, modelName: String): Result<File> {
        return try {
            if (!validateModelFile(sourceFile.absolutePath)) {
                return Result.failure(Exception("فایل مدل نامعتبر است"))
            }

            val modelsDir = getModelsDirectory()
            val targetFile = File(modelsDir, "${modelName}.gguf")

            sourceFile.copyTo(targetFile, overwrite = true)
            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getModelsDirectory(): File {
        val modelsDir = File(context.filesDir, "models")
        if (!modelsDir.exists()) {
            modelsDir.mkdirs()
        }
        return modelsDir
    }

    fun getModelSize(modelName: String): Long {
        val modelFile = File(getModelsDirectory(), "${modelName}.gguf")
        return if (modelFile.exists()) modelFile.length() else 0L
    }

    fun getTotalModelsSize(): Long {
        return getModelsDirectory().listFiles()?.sumOf { it.length() } ?: 0L
    }

    fun clearDownloadState() {
        _downloadState.value = DownloadState.Idle
    }

    data class ModelInfo(
        val name: String,
        val displayName: String,
        val sizeGB: Float,
        val downloadUrl: String,
        val description: String
    )

    sealed class DownloadState {
        object Idle : DownloadState()
        data class Downloading(val modelName: String, val progress: Float) : DownloadState()
        data class Completed(val modelName: String) : DownloadState()
        data class Error(val modelName: String, val message: String) : DownloadState()
    }
}
