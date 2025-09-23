package com.example.persianaiapp.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.persianaiapp.R
import com.example.persianaiapp.data.local.SettingsManager
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRecognitionHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager
) : RecognitionListener, EasyPermissions.PermissionCallbacks {

    private var speechRecognizer: SpeechRecognizer? = null
    private var recognitionListener: RecognitionListener? = null
    private var isListening = false
    private var lastPartialResult = ""
    
    // Callbacks
    var onResult: ((String) -> Unit)? = null
    var onPartialResult: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onReadyForSpeech: (() -> Unit)? = null
    var onEndOfSpeech: (() -> Unit)? = null
    
    init {
        initialize()
    }
    
    private fun initialize() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@SpeechRecognitionHelper)
            }
        } else {
            onError?.invoke(context.getString(R.string.speech_recognition_not_available))
        }
    }
    
    fun startListening() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }
        
        if (isListening) {
            stopListening()
        }
        
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, getLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            
            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            onError?.invoke("Failed to start speech recognition: ${e.message}")
            isListening = false
        }
    }
    
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechRecognition", "Error stopping speech recognition", e)
        } finally {
            isListening = false
        }
    }
    
    fun cancel() {
        try {
            speechRecognizer?.cancel()
        } catch (e: Exception) {
            Log.e("SpeechRecognition", "Error canceling speech recognition", e)
        } finally {
            isListening = false
        }
    }
    
    fun destroy() {
        cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
    
    private fun getLanguageTag(): String {
        return when (settingsManager.getSpeechRecognitionLanguage()) {
            "fa" -> "fa-IR" // Persian
            "en" -> "en-US" // English (US)
            else -> Locale.getDefault().toLanguageTag()
        }
    }
    
    // region RecognitionListener
    override fun onReadyForSpeech(params: Bundle?) {
        isListening = true
        onReadyForSpeech?.invoke()
    }
    
    override fun onBeginningOfSpeech() {
        // Not used
    }
    
    override fun onRmsChanged(rmsdB: Float) {
        // Not used
    }
    
    override fun onBufferReceived(buffer: ByteArray?) {
        // Not used
    }
    
    override fun onEndOfSpeech() {
        isListening = false
        onEndOfSpeech?.invoke()
    }
    
    override fun onError(error: Int) {
        isListening = false
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No recognition result matched"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error occurred: $error"
        }
        
        // Don't report certain errors to the user
        if (error != SpeechRecognizer.ERROR_NO_MATCH && 
            error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            onError?.invoke(errorMessage)
        }
    }
    
    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull() ?: return
        
        lastPartialResult = ""
        onResult?.invoke(text)
    }
    
    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val partialText = matches?.firstOrNull() ?: return
        
        if (partialText != lastPartialResult) {
            lastPartialResult = partialText
            onPartialResult?.invoke(partialText)
        }
    }
    
    override fun onEvent(eventType: Int, params: Bundle?) {
        // Not used
    }
    // endregion
    
    // region Permissions
    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermissions() {
        if (context is Activity) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }
    
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening()
            } else {
                onError?.invoke(context.getString(R.string.record_audio_permission_denied))
            }
        }
    }
    
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(context as Activity, perms)) {
            SettingsDialog.Builder(context as Activity).build().show()
        }
    }
    
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Permission granted, start listening
        startListening()
    }
    // endregion
    
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}
