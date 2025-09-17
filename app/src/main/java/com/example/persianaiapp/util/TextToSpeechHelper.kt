package com.example.persianaiapp.util

import android.content.Context
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: com.example.persianaiapp.data.local.SettingsManager
) : TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val pendingUtterances = mutableListOf<String>()
    private var onInitialized: (() -> Unit)? = null
    private var onError: ((String) -> Unit)? = null
    
    private val ttsListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            // Not used
        }
        
        override fun onDone(utteranceId: String?) {
            // Notify completion
        }
        
        override fun onError(utteranceId: String?, errorCode: Int) {
            val errorMsg = when (errorCode) {
                TextToSpeech.ERROR_INVALID_REQUEST -> "Invalid TTS request"
                TextToSpeech.ERROR_NETWORK -> "Network error in TTS"
                TextToSpeech.ERROR_NETWORK_TIMEOUT -> "Network timeout in TTS"
                TextToSpeech.ERROR_NOT_INSTALLED_YET -> "TTS data not installed"
                TextToSpeech.ERROR_OUTPUT -> "TTS output error"
                TextToSpeech.ERROR_SERVICE -> "TTS service error"
                TextToSpeech.ERROR_SYNTHESIS -> "TTS synthesis error"
                else -> "Unknown TTS error"
            }
            onError?.invoke(errorMsg)
            Log.e("TTS", "Error in TTS: $errorMsg")
        }
    }
    
    init {
        initialize()
    }
    
    private fun initialize() {
        if (tts == null) {
            tts = TextToSpeech(context, this)
            tts?.setOnUtteranceProgressListener(ttsListener)
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to Persian if available, otherwise use default
            val result = setLanguage(Locale("fa")) || 
                        setLanguage(Locale.ENGLISH) || 
                        setLanguage(Locale.getDefault())
            
            if (result) {
                isInitialized = true
                onInitialized?.invoke()
                processPendingUtterances()
            } else {
                onError?.invoke("TTS language not supported")
            }
        } else {
            onError?.invoke("TTS initialization failed")
        }
    }
    
    private fun setLanguage(locale: Locale): Boolean {
        return tts?.setLanguage(locale)?.let { result ->
            result != TextToSpeech.LANG_MISSING_DATA && 
            result != TextToSpeech.LANG_NOT_SUPPORTED
        } ?: false
    }
    
    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isInitialized) {
            pendingUtterances.add(text)
            onInitialized = { processPendingUtterances() }
            return
        }
        
        // Stop any ongoing speech
        stop()
        
        // Set audio stream and volume
        tts?.setAudioAttributes(
            android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                .build()
        )
        
        // Set speech rate and pitch
        tts?.setSpeechRate(settingsManager.getSpeechRate())
        tts?.setPitch(settingsManager.getSpeechPitch())
        
        // Speak the text
        val params = hashMapOf<String, String>().apply {
            put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, System.currentTimeMillis().toString())
        }
        
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "tts_${System.currentTimeMillis()}")
        
        if (result == TextToSpeech.ERROR) {
            onError?.invoke("Failed to speak text")
            onComplete?.invoke()
        } else {
            // Schedule completion callback after a delay
            android.os.Handler(context.mainLooper).postDelayed({
                onComplete?.invoke()
            }, calculateSpeechDuration(text))
        }
    }
    
    private fun calculateSpeechDuration(text: String): Long {
        // Average speech rate is about 150 words per minute
        val words = text.split("\\s+".toRegex()).size
        val minutes = words / 150.0
        return (minutes * 60 * 1000).toLong()
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
    
    fun setOnErrorListener(listener: (String) -> Unit) {
        onError = listener
    }
    
    private fun processPendingUtterances() {
        if (pendingUtterances.isNotEmpty()) {
            val text = pendingUtterances.joinToString(" ")
            pendingUtterances.clear()
            speak(text)
        }
    }
    
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }
    
    fun getAvailableLanguages(): Set<Locale> {
        return tts?.availableLanguages?.toSet() ?: emptySet()
    }
    
    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }
    
    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }
}
