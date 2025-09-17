package com.example.persianaiapp.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class PersianTTSEngine @Inject constructor(
    private val context: Context
) {
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("fa", "IR"))
                
                when (result) {
                    TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                        // Try alternative Persian locales
                        val alternativeResult = tts?.setLanguage(Locale("fa"))
                        if (alternativeResult == TextToSpeech.LANG_AVAILABLE || 
                            alternativeResult == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                            setupTTSSettings()
                            isInitialized = true
                            Timber.d("Persian TTS initialized with alternative locale")
                            continuation.resume(true)
                        } else {
                            Timber.w("Persian language not supported by TTS engine")
                            continuation.resume(false)
                        }
                    }
                    TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE -> {
                        setupTTSSettings()
                        isInitialized = true
                        Timber.d("Persian TTS initialized successfully")
                        continuation.resume(true)
                    }
                    else -> {
                        Timber.e("Failed to set Persian language for TTS")
                        continuation.resume(false)
                    }
                }
            } else {
                Timber.e("TTS initialization failed with status: $status")
                continuation.resume(false)
            }
        }
    }
    
    private fun setupTTSSettings() {
        tts?.apply {
            setSpeechRate(0.9f) // Slightly slower for better Persian pronunciation
            setPitch(1.0f)
            
            // Set utterance progress listener
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Timber.d("TTS started for utterance: $utteranceId")
                }
                
                override fun onDone(utteranceId: String?) {
                    Timber.d("TTS completed for utterance: $utteranceId")
                }
                
                override fun onError(utteranceId: String?) {
                    Timber.e("TTS error for utterance: $utteranceId")
                }
            })
        }
    }
    
    fun speak(text: String, utteranceId: String = UUID.randomUUID().toString()): Boolean {
        if (!isInitialized || tts == null) {
            Timber.w("TTS not initialized")
            return false
        }
        
        return try {
            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            result == TextToSpeech.SUCCESS
        } catch (e: Exception) {
            Timber.e(e, "Error speaking text: $text")
            false
        }
    }
    
    fun speakQueued(text: String, utteranceId: String = UUID.randomUUID().toString()): Boolean {
        if (!isInitialized || tts == null) {
            Timber.w("TTS not initialized")
            return false
        }
        
        return try {
            val result = tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
            result == TextToSpeech.SUCCESS
        } catch (e: Exception) {
            Timber.e(e, "Error queuing speech: $text")
            false
        }
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }
    
    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }
    
    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }
    
    fun getAvailableLanguages(): Set<Locale>? {
        return tts?.availableLanguages
    }
    
    fun isPersianSupported(): Boolean {
        val persianLocale = Locale("fa", "IR")
        return tts?.isLanguageAvailable(persianLocale) == TextToSpeech.LANG_AVAILABLE ||
               tts?.isLanguageAvailable(persianLocale) == TextToSpeech.LANG_COUNTRY_AVAILABLE
    }
    
    fun cleanup() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
