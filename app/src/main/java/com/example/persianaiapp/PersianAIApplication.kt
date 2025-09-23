package com.example.persianaiapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import javax.inject.Inject

@HiltAndroidApp
class PersianAIApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        
        try {
            // Initialize Timber for logging in debug builds
            if (BuildConfig.DEBUG) {
                try {
                    Timber.plant(Timber.DebugTree())
                } catch (e: Exception) {
                    android.util.Log.e("PersianAIApplication", "Error initializing Timber", e)
                }
            }
            
            // Create notification channels for Android O and above
            createNotificationChannels()
            
            android.util.Log.d("PersianAIApplication", "PersianAIApplication initialized successfully")
        } catch (e: Exception) {
            // Log the error but don't crash the app
            android.util.Log.e("PersianAIApplication", "Error during initialization", e)
            e.printStackTrace()
        }
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // Voice Input Channel
                val voiceInputChannel = NotificationChannel(
                    CHANNEL_VOICE_INPUT,
                    getString(R.string.channel_voice_input_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.channel_voice_input_description)
                }
                
                // AI Response Channel
                val aiResponseChannel = NotificationChannel(
                    CHANNEL_AI_RESPONSE,
                    getString(R.string.channel_ai_response_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.channel_ai_response_description)
                }
                
                // Background Processing Channel
                val backgroundChannel = NotificationChannel(
                    CHANNEL_BACKGROUND_PROCESSING,
                    getString(R.string.channel_background_processing_name),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = getString(R.string.channel_background_processing_description)
                }
                
                // Reminders Channel
                val remindersChannel = NotificationChannel(
                    CHANNEL_REMINDERS,
                    getString(R.string.channel_reminders_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.channel_reminders_description)
                }
                
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannels(
                    listOf(voiceInputChannel, aiResponseChannel, backgroundChannel, remindersChannel)
                )
            } catch (e: Exception) {
                android.util.Log.e("PersianAIApplication", "Error creating notification channels", e)
                e.printStackTrace()
            }
        }
    }
    
    companion object {
        const val CHANNEL_VOICE_INPUT = "voice_input"
        const val CHANNEL_AI_RESPONSE = "ai_response"
        const val CHANNEL_BACKGROUND_PROCESSING = "background_processing"
        const val CHANNEL_VOICE_SERVICE = "voice_service"
        const val CHANNEL_AI_RESPONSES = "ai_responses"
        const val CHANNEL_REMINDERS = "reminders"
    }
}
