package com.example.persianaiapp.voice

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceRecorder @Inject constructor(
    private val context: Context
) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var model: Model? = null
    private var recognizer: Recognizer? = null

    init {
        initializeVosk()
    }

    private fun initializeVosk() {
        try {
            LibVosk.setLogLevel(LogLevel.WARNINGS)
            // Initialize with a small Persian model if available
            // For now, we'll use a placeholder - in production, you'd download the actual model
            val modelPath = File(context.filesDir, "vosk-model-small-fa-0.5")
            if (modelPath.exists()) {
                model = Model(modelPath.absolutePath)
                recognizer = Recognizer(model, 16000.0f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun startRecording(): File = withContext(Dispatchers.IO) {
        val audioDir = File(context.filesDir, "audio")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }

        outputFile = File(audioDir, "recording_${System.currentTimeMillis()}.m4a")

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile!!.absolutePath)
            setAudioSamplingRate(16000)
            setAudioEncodingBitRate(64000)

            prepare()
            start()
        }

        outputFile!!
    }

    suspend fun stopRecording(): File? = withContext(Dispatchers.IO) {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun cancelRecording() = withContext(Dispatchers.IO) {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            outputFile?.delete()
            outputFile = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun transcribeAudio(audioFile: File): String = withContext(Dispatchers.IO) {
        try {
            if (recognizer == null) {
                return@withContext "مدل تشخیص گفتار در دسترس نیست"
            }

            // Convert audio file to PCM format for Vosk
            val pcmFile = convertToPCM(audioFile)
            
            val inputStream = FileInputStream(pcmFile)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            var result = ""

            while (inputStream.read(buffer).also { bytesRead = it } >= 0) {
                if (recognizer!!.acceptWaveForm(buffer, bytesRead)) {
                    val partialResult = recognizer!!.result
                    // Parse JSON result to extract text
                    result += parseVoskResult(partialResult)
                }
            }

            val finalResult = recognizer!!.finalResult
            result += parseVoskResult(finalResult)

            inputStream.close()
            pcmFile.delete()

            result.ifBlank { "متن قابل تشخیص نیست" }
        } catch (e: Exception) {
            e.printStackTrace()
            "خطا در تبدیل صدا به متن: ${e.message}"
        }
    }

    private fun convertToPCM(audioFile: File): File {
        // Placeholder for audio conversion
        // In production, you'd use FFmpeg or similar to convert M4A to PCM
        val pcmFile = File(audioFile.parent, "${audioFile.nameWithoutExtension}.pcm")
        
        // For now, just copy the file (this won't work in production)
        audioFile.copyTo(pcmFile, overwrite = true)
        
        return pcmFile
    }

    private fun parseVoskResult(jsonResult: String): String {
        return try {
            // Simple JSON parsing - in production, use a proper JSON library
            val textStart = jsonResult.indexOf("\"text\" : \"") + 10
            val textEnd = jsonResult.indexOf("\"", textStart)
            if (textStart > 9 && textEnd > textStart) {
                jsonResult.substring(textStart, textEnd)
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun cleanup() {
        try {
            mediaRecorder?.release()
            recognizer?.close()
            model?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
