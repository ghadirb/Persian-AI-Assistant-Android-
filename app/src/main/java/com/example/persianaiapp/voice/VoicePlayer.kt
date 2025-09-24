package com.example.persianaiapp.voice

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoicePlayer @Inject constructor(
    private val context: Context
) : TextToSpeech.OnInitListener {
    
    private var mediaPlayer: MediaPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        initializeTextToSpeech()
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                // Try to set Persian language
                val persianLocale = Locale("fa", "IR")
                val result = tts.setLanguage(persianLocale)
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to English if Persian is not available
                    tts.setLanguage(Locale.US)
                }
                
                // Configure TTS settings
                tts.setSpeechRate(0.9f)
                tts.setPitch(1.0f)
                
                isTtsInitialized = true
            }
        }
    }

    suspend fun playAudio(audioFilePath: String) = withContext(Dispatchers.IO) {
        try {
            stopPlayback()
            
            val audioFile = File(audioFilePath)
            if (!audioFile.exists()) {
                throw Exception("فایل صوتی یافت نشد")
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener { 
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { _, what, extra ->
                    release()
                    mediaPlayer = null
                    true
                }
            }
        } catch (e: Exception) {
            throw Exception("خطا در پخش فایل صوتی: ${e.message}")
        }
    }

    suspend fun speakText(text: String) = withContext(Dispatchers.Main) {
        try {
            if (!isTtsInitialized) {
                throw Exception("سیستم تبدیل متن به گفتار آماده نیست")
            }

            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            throw Exception("خطا در تولید گفتار: ${e.message}")
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                mediaPlayer = null
            }
            
            textToSpeech?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true || textToSpeech?.isSpeaking == true
    }

    fun setPlaybackSpeed(speed: Float) {
        textToSpeech?.setSpeechRate(speed)
    }

    fun setPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch)
    }

    fun cleanup() {
        try {
            stopPlayback()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
