package com.example.persianaiapp.domain.repository

import com.example.persianaiapp.data.local.entity.UserSettings
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    fun getSettings(): Flow<UserSettings?>
    
    fun getUserSettings(): Flow<UserSettings?>
    
    suspend fun getUserSettingsSync(): UserSettings?
    
    suspend fun saveUserSettings(settings: UserSettings)
    
    suspend fun updateOnlineMode(isOnline: Boolean)
    
    suspend fun updateSelectedModel(model: String)
    
    suspend fun updateVoiceEnabled(enabled: Boolean)
    
    suspend fun updateEncryptedApiKeys(keys: String)
    
    suspend fun updateLocalModelPath(path: String)
    
    suspend fun updateLastBackupTime(time: Long)
    
    suspend fun updateLastKeyRefresh(time: Long)
    
    suspend fun updateFirstLaunch(isFirst: Boolean)
    
    suspend fun updatePasswordHash(hash: String)
    
    suspend fun updateBiometricEnabled(enabled: Boolean)
    
    suspend fun updateSettings(update: (UserSettings) -> UserSettings)
    
    suspend fun updateLanguage(language: String)
    
    suspend fun updateTheme(theme: String)
    
    suspend fun updateVoiceActivationEnabled(enabled: Boolean)
    
    suspend fun updateVoiceFeedbackEnabled(enabled: Boolean)
    
    suspend fun updateAutoBackupEnabled(enabled: Boolean)
    
    suspend fun updateAutoModelUpdateEnabled(enabled: Boolean)
}
