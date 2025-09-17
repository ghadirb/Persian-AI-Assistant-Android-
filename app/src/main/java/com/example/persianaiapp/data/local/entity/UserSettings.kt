package com.example.persianaiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val isOnlineMode: Boolean = false,
    val selectedModel: String = "",
    val voiceEnabled: Boolean = true,
    val autoBackup: Boolean = true,
    val backupInterval: Long = 24 * 60 * 60 * 1000, // 24 hours in milliseconds
    val language: String = "fa", // Persian by default
    val theme: String = "auto", // auto, light, dark
    val fontSize: Float = 16f,
    val voiceSpeed: Float = 1.0f,
    val isFirstLaunch: Boolean = true,
    val lastBackupTime: Long = 0,
    val encryptedApiKeys: String = "",
    val localModelPath: String = "",
    val notificationsEnabled: Boolean = true,
    val backgroundProcessing: Boolean = true,
    val biometricEnabled: Boolean = false,
    val passwordHash: String = "",
    val lastKeyRefresh: Long = 0,
    val voiceActivationEnabled: Boolean = false,
    val voiceFeedbackEnabled: Boolean = true,
    val autoBackupEnabled: Boolean = false,
    val autoModelUpdateEnabled: Boolean = false,
    val encryptedBackupEnabled: Boolean = false,
    val googleDriveBackupEnabled: Boolean = false,
    val autoDownloadModelUpdates: Boolean = false
)
