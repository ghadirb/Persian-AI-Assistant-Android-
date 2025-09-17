package com.example.persianaiapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.persianaiapp.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val IS_OFFLINE_MODE = booleanPreferencesKey("is_offline_mode")
        val SELECTED_MODEL = stringPreferencesKey("selected_model")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val THEME = stringPreferencesKey("theme")
        val FONT_SIZE = intPreferencesKey("font_size")
        val LAST_BACKUP = longPreferencesKey("last_backup")
        val GOOGLE_DRIVE_BACKUP_ENABLED = booleanPreferencesKey("google_drive_backup_enabled")
        val AUTO_BACKUP_ENABLED = booleanPreferencesKey("auto_backup_enabled")
        val AUTO_BACKUP_FREQUENCY = intPreferencesKey("auto_backup_frequency") // in hours
    }

    // Offline Mode
    val isOfflineMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_OFFLINE_MODE] ?: false
        }

    suspend fun setOfflineMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_OFFLINE_MODE] = enabled
        }
    }
    
    suspend fun isOfflineMode(): Boolean {
        return isOfflineMode.first()
    }

    // Selected Model
    val selectedModel: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_MODEL] ?: "default"
        }

    suspend fun setSelectedModel(modelId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_MODEL] = modelId
        }
    }

    // Voice Settings
    val isVoiceEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.VOICE_ENABLED] ?: true
        }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOICE_ENABLED] = enabled
        }
    }

    // Notifications
    val areNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // Theme
    val theme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME] ?: "system"
        }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    // Font Size
    val fontSize: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] ?: 16
        }

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = size
        }
    }

    // Backup Settings
    val lastBackup: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_BACKUP] ?: 0L
        }

    suspend fun updateLastBackup(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_BACKUP] = timestamp
        }
    }

    val isGoogleDriveBackupEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.GOOGLE_DRIVE_BACKUP_ENABLED] ?: false
        }

    suspend fun setGoogleDriveBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GOOGLE_DRIVE_BACKUP_ENABLED] = enabled
        }
    }

    val isAutoBackupEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_BACKUP_ENABLED] ?: false
        }

    suspend fun setAutoBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_BACKUP_ENABLED] = enabled
        }
    }

    val autoBackupFrequency: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_BACKUP_FREQUENCY] ?: 24 // Default: 24 hours
        }

    suspend fun setAutoBackupFrequency(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_BACKUP_FREQUENCY] = hours
        }
    }

    // Helper function to get all settings as a map
    val allSettings: Flow<Map<String, Any?>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapOf(
                "isOfflineMode" to preferences[PreferencesKeys.IS_OFFLINE_MODE] ?: false,
                "selectedModel" to preferences[PreferencesKeys.SELECTED_MODEL] ?: "default",
                "isVoiceEnabled" to preferences[PreferencesKeys.VOICE_ENABLED] ?: true,
                "areNotificationsEnabled" to preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                "theme" to preferences[PreferencesKeys.THEME] ?: "system",
                "fontSize" to preferences[PreferencesKeys.FONT_SIZE] ?: 16,
                "lastBackup" to preferences[PreferencesKeys.LAST_BACKUP] ?: 0L,
                "isGoogleDriveBackupEnabled" to preferences[PreferencesKeys.GOOGLE_DRIVE_BACKUP_ENABLED] ?: false,
                "isAutoBackupEnabled" to preferences[PreferencesKeys.AUTO_BACKUP_ENABLED] ?: false,
                "autoBackupFrequency" to preferences[PreferencesKeys.AUTO_BACKUP_FREQUENCY] ?: 24
            )
        }

    // API Keys for AI services
    private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
    private val ANTHROPIC_API_KEY = stringPreferencesKey("anthropic_api_key")
    private val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
    private val AIML_API_KEY = stringPreferencesKey("aiml_api_key")

    val openAIApiKey: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[OPENAI_API_KEY] ?: ""
        }

    suspend fun setOpenAIApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[OPENAI_API_KEY] = apiKey
        }
    }

    fun getOpenAIApiKey(): String {
        // Run blocking since this is called from non-coroutine context
        return runBlocking {
            openAIApiKey.first()
        }
    }

    val anthropicApiKey: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[ANTHROPIC_API_KEY] ?: ""
        }

    suspend fun setAnthropicApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[ANTHROPIC_API_KEY] = apiKey
        }
    }

    fun getAnthropicApiKey(): String {
        return runBlocking {
            anthropicApiKey.first()
        }
    }

    val openRouterApiKey: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[OPENROUTER_API_KEY] ?: ""
        }

    suspend fun setOpenRouterApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[OPENROUTER_API_KEY] = apiKey
        }
    }

    fun getOpenRouterApiKey(): String {
        return runBlocking {
            openRouterApiKey.first()
        }
    }

    val aimlApiKey: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[AIML_API_KEY] ?: ""
        }

    suspend fun setAimlApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[AIML_API_KEY] = apiKey
        }
    }

    fun getAimlApiKey(): String {
        return runBlocking {
            aimlApiKey.first()
        }
    }
}