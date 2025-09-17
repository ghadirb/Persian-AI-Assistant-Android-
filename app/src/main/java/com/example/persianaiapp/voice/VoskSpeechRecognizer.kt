package com.example.persianaiapp.voice

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoskSpeechRecognizer @Inject constructor(
    private val context: Context
) {
    
    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var isInitialized = false
    
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) return@withContext true
            
            // Check if Persian model exists
            val modelPath = File(context.filesDir, "vosk-model-small-fa-0.5")
            
            if (!modelPath.exists()) {
                Timber.w("Persian Vosk model not found at: ${modelPath.absolutePath}")
                return@withContext false
            }
            
            model = Model(modelPath.absolutePath)
            isInitialized = true
            Timber.d("Vosk Persian model initialized successfully")
            true
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Vosk model")
            false
        }
    }
    
    suspend fun downloadPersianModel(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Download Persian model from Vosk repository
            val modelUrl = "https://alphacephei.com/vosk/models/vosk-model-small-fa-0.5.zip"
            val modelDir = File(context.filesDir, "vosk-model-small-fa-0.5")
            
            if (modelDir.exists()) {
                Timber.d("Persian model already exists")
                return@withContext true
            }
            
            // Use StorageService to download and extract model
            StorageService.unpack(context, "vosk-model-small-fa-0.5", 
                "model-fa.zip", { model ->
                    this@VoskSpeechRecognizer.model = model
                    isInitialized = true
                    Timber.d("Persian model downloaded and initialized")
                }, { exception ->
                    Timber.e(exception, "Failed to download Persian model")
                })
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Error downloading Persian model")
            false
        }
    }
    
    fun startListening(listener: SpeechRecognitionListener) {
        if (!isInitialized || model == null) {
            listener.onError("Vosk model not initialized")
            return
        }
        
        try {
            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)
            
            speechService?.startListening(object : RecognitionListener {
                override fun onPartialResult(hypothesis: String?) {
                    hypothesis?.let { 
                        try {
                            val json = JSONObject(it)
                            val partial = json.optString("partial", "")
                            if (partial.isNotEmpty()) {
                                listener.onPartialResult(partial)
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing partial result")
                        }
                    }
                }
                
                override fun onResult(hypothesis: String?) {
                    hypothesis?.let {
                        try {
                            val json = JSONObject(it)
                            val text = json.optString("text", "")
                            if (text.isNotEmpty()) {
                                listener.onFinalResult(text)
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing final result")
                        }
                    }
                }
                
                override fun onFinalResult(hypothesis: String?) {
                    onResult(hypothesis)
                }
                
                override fun onError(exception: Exception?) {
                    listener.onError(exception?.message ?: "Unknown speech recognition error")
                }
                
                override fun onTimeout() {
                    listener.onTimeout()
                }
            })
            
        } catch (e: IOException) {
            listener.onError("Failed to start speech recognition: ${e.message}")
        }
    }
    
    fun stopListening() {
        speechService?.stop()
        speechService = null
    }
    
    fun cancel() {
        speechService?.cancel()
        speechService = null
    }
    
    fun isListening(): Boolean {
        return speechService != null
    }
    
    fun cleanup() {
        stopListening()
        model?.close()
        model = null
        isInitialized = false
    }
    
    interface SpeechRecognitionListener {
        fun onPartialResult(text: String)
        fun onFinalResult(text: String)
        fun onError(error: String)
        fun onTimeout()
    }
}
