package com.example.persianaiapp.data.repository

import com.example.persianaiapp.data.local.dao.UserSettingsDao
import com.example.persianaiapp.data.local.entity.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) : ISettingsRepository {
    
    override fun getSettings(): Flow<UserSettings?> = userSettingsDao.getUserSettings()
    
    override fun getUserSettings(): Flow<UserSettings?> = userSettingsDao.getUserSettings()
    
    override suspend fun getUserSettingsSync(): UserSettings? = userSettingsDao.getUserSettingsSync()
    
    override suspend fun saveUserSettings(settings: UserSettings) = userSettingsDao.insertUserSettings(settings)
    
    override suspend fun updateOnlineMode(isOnline: Boolean) = userSettingsDao.updateOnlineMode(isOnline)
    
    override suspend fun updateSelectedModel(model: String) = userSettingsDao.updateSelectedModel(model)
    
    override suspend fun updateVoiceEnabled(enabled: Boolean) = userSettingsDao.updateVoiceEnabled(enabled)
    
    override suspend fun updateEncryptedApiKeys(keys: String) = userSettingsDao.updateEncryptedApiKeys(keys)
    
    override suspend fun updateLocalModelPath(path: String) = userSettingsDao.updateLocalModelPath(path)
    
    override suspend fun updateLastBackupTime(time: Long) = userSettingsDao.updateLastBackupTime(time)
    
    override suspend fun updateLastKeyRefresh(time: Long) = userSettingsDao.updateLastKeyRefresh(time)
    
    override suspend fun updateFirstLaunch(isFirst: Boolean) = userSettingsDao.updateFirstLaunch(isFirst)
    
    override suspend fun updatePasswordHash(hash: String) = userSettingsDao.updatePasswordHash(hash)
    
    override suspend fun updateBiometricEnabled(enabled: Boolean) = userSettingsDao.updateBiometricEnabled(enabled)
    
    override suspend fun updateSettings(update: (UserSettings) -> UserSettings) {
        val currentSettings = getUserSettingsSync() ?: UserSettings()
        val updatedSettings = update(currentSettings)
        saveUserSettings(updatedSettings)
    }
    
    override suspend fun updateLanguage(language: String) {
        updateSettings { it.copy(language = language) }
    }
    
    override suspend fun updateTheme(theme: String) {
        updateSettings { it.copy(theme = theme) }
    }
    
    override suspend fun updateVoiceActivationEnabled(enabled: Boolean) {
        updateSettings { it.copy(voiceActivationEnabled = enabled) }
    }
    
    override suspend fun updateVoiceFeedbackEnabled(enabled: Boolean) {
        updateSettings { it.copy(voiceFeedbackEnabled = enabled) }
    }
    
    override suspend fun updateAutoBackupEnabled(enabled: Boolean) {
        updateSettings { it.copy(autoBackupEnabled = enabled) }
    }
    
    override suspend fun updateAutoModelUpdateEnabled(enabled: Boolean) {
        updateSettings { it.copy(autoModelUpdateEnabled = enabled) }
    }
}
