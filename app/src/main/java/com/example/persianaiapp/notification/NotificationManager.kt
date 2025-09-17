package com.example.persianaiapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.persianaiapp.PersianAIApplication
import com.example.persianaiapp.R
import com.example.persianaiapp.ui.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNotificationManager @Inject constructor(
    private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // AI Response Channel
            val aiResponseChannel = NotificationChannel(
                PersianAIApplication.CHANNEL_AI_RESPONSES,
                context.getString(R.string.channel_ai_responses_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_ai_responses_description)
                setShowBadge(true)
                enableVibration(true)
            }

            // Reminder Channel
            val reminderChannel = NotificationChannel(
                PersianAIApplication.CHANNEL_REMINDERS,
                context.getString(R.string.channel_reminders_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_reminders_description)
                setShowBadge(true)
                enableVibration(true)
                enableLights(true)
            }

            // Background Processing Channel
            val backgroundChannel = NotificationChannel(
                PersianAIApplication.CHANNEL_BACKGROUND_PROCESSING,
                context.getString(R.string.channel_background_processing_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.channel_background_processing_description)
                setShowBadge(false)
            }

            // Voice Service Channel
            val voiceChannel = NotificationChannel(
                PersianAIApplication.CHANNEL_VOICE_SERVICE,
                context.getString(R.string.channel_voice_service_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.channel_voice_service_description)
                setShowBadge(false)
            }

            notificationManager.createNotificationChannels(listOf(
                aiResponseChannel,
                reminderChannel,
                backgroundChannel,
                voiceChannel
            ))
        }
    }

    fun showAIResponseNotification(message: String, conversationId: Long? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            conversationId?.let { putExtra("conversation_id", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PersianAIApplication.CHANNEL_AI_RESPONSES)
            .setSmallIcon(R.drawable.ic_ai_assistant)
            .setContentTitle(context.getString(R.string.ai_response))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_AI_RESPONSE, notification)
    }

    fun showReminderNotification(title: String, message: String, reminderId: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminder_id", reminderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, reminderId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PersianAIApplication.CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(reminderId.toInt(), notification)
    }

    fun showVoiceActivationNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PersianAIApplication.CHANNEL_VOICE_SERVICE)
            .setSmallIcon(R.drawable.ic_mic)
            .setContentTitle(context.getString(R.string.voice_activation_active))
            .setContentText(context.getString(R.string.voice_activation_listening))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(NOTIFICATION_ID_VOICE_ACTIVATION, notification)
    }

    fun showBackupCompletedNotification(backupType: String) {
        val notification = NotificationCompat.Builder(context, PersianAIApplication.CHANNEL_BACKGROUND_PROCESSING)
            .setSmallIcon(R.drawable.ic_backup)
            .setContentTitle(context.getString(R.string.backup_completed))
            .setContentText(context.getString(R.string.backup_completed_message, backupType))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BACKUP, notification)
    }

    fun showModelUpdateNotification(modelName: String) {
        val notification = NotificationCompat.Builder(context, PersianAIApplication.CHANNEL_BACKGROUND_PROCESSING)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(context.getString(R.string.model_updated))
            .setContentText(context.getString(R.string.model_updated_message, modelName))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_MODEL_UPDATE, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    companion object {
        const val NOTIFICATION_ID_AI_RESPONSE = 1001
        const val NOTIFICATION_ID_REMINDER = 1002
        const val NOTIFICATION_ID_VOICE_ACTIVATION = 1003
        const val NOTIFICATION_ID_BACKUP = 1004
        const val NOTIFICATION_ID_MODEL_UPDATE = 1005
    }
}
