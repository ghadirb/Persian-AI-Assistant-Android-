package com.example.persianaiapp.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineModelDownloader @Inject constructor(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    data class DownloadProgress(
        val bytesDownloaded: Long,
        val totalBytes: Long,
        val percentage: Int,
        val isComplete: Boolean = false,
        val error: String? = null
    )

    data class AvailableModel(
        val name: String,
        val displayName: String,
        val description: String,
        val downloadUrl: String,
        val sizeBytes: Long,
        val type: ModelType,
        val language: String = "fa"
    )

    enum class ModelType {
        VOSK_SPEECH, GGUF_LLM, ONNX_LLM, TFLITE_LLM
    }

    companion object {
        private val AVAILABLE_MODELS = listOf(
            AvailableModel(
                name = "vosk-model-small-fa-0.5",
                displayName = "مدل تشخیص گفتار فارسی کوچک",
                description = "مدل کوچک و سریع برای تشخیص گفتار فارسی",
                downloadUrl = "https://alphacephei.com/vosk/models/vosk-model-small-fa-0.5.zip",
                sizeBytes = 42 * 1024 * 1024, // 42MB
                type = ModelType.VOSK_SPEECH
            ),
            AvailableModel(
                name = "vosk-model-fa-0.22",
                displayName = "مدل تشخیص گفتار فارسی بزرگ",
                description = "مدل دقیق‌تر برای تشخیص گفتار فارسی",
                downloadUrl = "https://alphacephei.com/vosk/models/vosk-model-fa-0.22.zip",
                sizeBytes = 1300 * 1024 * 1024, // 1.3GB
                type = ModelType.VOSK_SPEECH
            ),
            AvailableModel(
                name = "llama-2-7b-chat-q4_0",
                displayName = "Llama 2 Chat 7B (کوانتایز شده)",
                description = "مدل چت فارسی بر پایه Llama 2",
                downloadUrl = "https://huggingface.co/TheBloke/Llama-2-7B-Chat-GGML/resolve/main/llama-2-7b-chat.q4_0.bin",
                sizeBytes = 3800 * 1024 * 1024, // 3.8GB
                type = ModelType.GGUF_LLM
            ),
            AvailableModel(
                name = "persian-gpt-small",
                displayName = "GPT فارسی کوچک",
                description = "مدل کوچک GPT برای زبان فارسی",
                downloadUrl = "https://example.com/persian-gpt-small.onnx", // Placeholder
                sizeBytes = 500 * 1024 * 1024, // 500MB
                type = ModelType.ONNX_LLM
            )
        )
    }

    fun getAvailableModels(): List<AvailableModel> = AVAILABLE_MODELS

    fun getModelsDirectory(): File {
        val modelsDir = File(context.getExternalFilesDir(null), "models")
        if (!modelsDir.exists()) {
            modelsDir.mkdirs()
        }
        return modelsDir
    }

    fun getInstalledModels(): List<File> {
        val modelsDir = getModelsDirectory()
        return modelsDir.listFiles()?.filter { file ->
            file.isFile && (
                file.extension.lowercase() in listOf("gguf", "bin", "onnx", "tflite") ||
                file.name.startsWith("vosk-model")
            )
        } ?: emptyList()
    }

    fun isModelInstalled(modelName: String): Boolean {
        val modelsDir = getModelsDirectory()
        return File(modelsDir, modelName).exists() || 
               File(modelsDir, "$modelName.zip").exists() ||
               modelsDir.listFiles()?.any { it.name.contains(modelName) } == true
    }

    suspend fun downloadModel(model: AvailableModel): Flow<DownloadProgress> = flow {
        try {
            val modelsDir = getModelsDirectory()
            val outputFile = File(modelsDir, "${model.name}.${getFileExtension(model.downloadUrl)}")

            if (outputFile.exists()) {
                emit(DownloadProgress(outputFile.length(), outputFile.length(), 100, true))
                return@flow
            }

            val request = Request.Builder()
                .url(model.downloadUrl)
                .build()

            withContext(Dispatchers.IO) {
                val response = okHttpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    emit(DownloadProgress(0, 0, 0, false, "دانلود ناموفق: ${response.code}"))
                    return@withContext
                }

                val body = response.body
                if (body == null) {
                    emit(DownloadProgress(0, 0, 0, false, "پاسخ خالی از سرور"))
                    return@withContext
                }

                val totalBytes = body.contentLength()
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(outputFile)

                val buffer = ByteArray(8192)
                var bytesDownloaded = 0L
                var bytesRead: Int

                try {
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        bytesDownloaded += bytesRead

                        val percentage = if (totalBytes > 0) {
                            ((bytesDownloaded * 100) / totalBytes).toInt()
                        } else 0

                        emit(DownloadProgress(bytesDownloaded, totalBytes, percentage))
                    }

                    // Handle zip files (extract if needed)
                    if (outputFile.extension.lowercase() == "zip") {
                        extractZipFile(outputFile, modelsDir)
                    }

                    emit(DownloadProgress(bytesDownloaded, totalBytes, 100, true))
                } finally {
                    inputStream.close()
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            Timber.e(e, "Error downloading model: ${model.name}")
            emit(DownloadProgress(0, 0, 0, false, "خطا در دانلود: ${e.message}"))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error downloading model: ${model.name}")
            emit(DownloadProgress(0, 0, 0, false, "خطای غیرمنتظره: ${e.message}"))
        }
    }

    private fun getFileExtension(url: String): String {
        return url.substringAfterLast('.').substringBefore('?')
    }

    private suspend fun extractZipFile(zipFile: File, destinationDir: File) {
        withContext(Dispatchers.IO) {
            try {
                val process = ProcessBuilder()
                    .command("powershell", "-Command", 
                        "Expand-Archive -Path '${zipFile.absolutePath}' -DestinationPath '${destinationDir.absolutePath}' -Force")
                    .start()
                
                process.waitFor()
                
                // Delete zip file after extraction
                if (zipFile.exists()) {
                    zipFile.delete()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error extracting zip file: ${zipFile.name}")
            }
        }
    }

    suspend fun deleteModel(modelName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val modelsDir = getModelsDirectory()
                val modelFile = File(modelsDir, modelName)
                
                if (modelFile.exists()) {
                    if (modelFile.isDirectory) {
                        modelFile.deleteRecursively()
                    } else {
                        modelFile.delete()
                    }
                } else {
                    // Try to find and delete by partial name match
                    modelsDir.listFiles()?.forEach { file ->
                        if (file.name.contains(modelName)) {
                            if (file.isDirectory) {
                                file.deleteRecursively()
                            } else {
                                file.delete()
                            }
                        }
                    }
                }
                true
            } catch (e: Exception) {
                Timber.e(e, "Error deleting model: $modelName")
                false
            }
        }
    }

    fun getModelSize(modelName: String): Long {
        val modelsDir = getModelsDirectory()
        val modelFile = File(modelsDir, modelName)
        
        return if (modelFile.exists()) {
            if (modelFile.isDirectory) {
                modelFile.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            } else {
                modelFile.length()
            }
        } else {
            0L
        }
    }

    fun formatFileSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return "%.1f %s".format(size, units[unitIndex])
    }
}
