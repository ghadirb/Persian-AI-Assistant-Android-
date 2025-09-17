package com.example.persianaiapp.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.persianaiapp.PersianAIApplication
import com.example.persianaiapp.R
import com.example.persianaiapp.data.repository.ISettingsRepository
import com.example.persianaiapp.ui.MainActivity
import com.example.persianaiapp.voice.VoiceRecorder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class VoiceActivationService : Service() {

    @Inject
    lateinit var voiceRecorder: VoiceRecorder
    
    @Inject
    lateinit var settingsRepository: ISettingsRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isListening = false

    companion object {
        const val ACTION_START_LISTENING = "START_LISTENING"
        const val ACTION_STOP_LISTENING = "STOP_LISTENING"
        const val NOTIFICATION_ID = 1002
        
        fun startService(context: Context) {
            val intent = Intent(context, VoiceActivationService::class.java).apply {
                action = ACTION_START_LISTENING
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, VoiceActivationService::class.java).apply {
                action = ACTION_STOP_LISTENING
            }
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_LISTENING -> {
                startForegroundService()
                startVoiceActivation()
            }
            ACTION_STOP_LISTENING -> {
                stopVoiceActivation()
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, PersianAIApplication.CHANNEL_VOICE_SERVICE)
            .setContentTitle(getString(R.string.voice_service_running))
            .setContentText(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    private fun startVoiceActivation() {
        if (isListening) return
        
        isListening = true
        serviceScope.launch {
            while (isListening) {
                try {
                    // Check if voice activation is enabled in settings
                    val settings = settingsRepository.getUserSettingsSync()
                    if (settings?.voiceActivationEnabled == true) {
                        // Listen for wake word or activation phrase
                        listenForActivation()
                    }
                    
                    delay(1000) // Check every second
                    
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(5000) // Wait longer on error
                }
            }
        }
    }

    private suspend fun listenForActivation() {
        // TODO: Implement wake word detection
        // This could use a lightweight model to detect Persian wake words
        // like "سلام دستیار" or "هوش مصنوعی"
        
        // For now, this is a placeholder that would:
        // 1. Continuously listen for audio
        // 2. Process audio through a wake word detection model
        // 3. When wake word is detected, launch the main app or start recording
        // 4. Handle privacy concerns by only processing locally
    }

    private fun stopVoiceActivation() {
        isListening = false
        serviceScope.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVoiceActivation()
    }
}
