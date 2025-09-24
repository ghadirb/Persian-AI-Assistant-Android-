package com.example.persianaiapp.data.local.dao

import androidx.room.*
import com.example.persianaiapp.data.local.entity.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserSettingsSync(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettings)

    @Update
    suspend fun updateUserSettings(settings: UserSettings)

    @Query("UPDATE user_settings SET isOnlineMode = :isOnlineMode WHERE id = 1")
    suspend fun updateOnlineMode(isOnlineMode: Boolean)

    @Query("UPDATE user_settings SET selectedModel = :model WHERE id = 1")
    suspend fun updateSelectedModel(model: String)

    @Query("UPDATE user_settings SET voiceEnabled = :enabled WHERE id = 1")
    suspend fun updateVoiceEnabled(enabled: Boolean)

    @Query("UPDATE user_settings SET encryptedApiKeys = :keys WHERE id = 1")
    suspend fun updateEncryptedApiKeys(keys: String)

    @Query("UPDATE user_settings SET localModelPath = :path WHERE id = 1")
    suspend fun updateLocalModelPath(path: String)

    @Query("UPDATE user_settings SET lastBackupTime = :time WHERE id = 1")
    suspend fun updateLastBackupTime(time: Long)

    @Query("UPDATE user_settings SET lastKeyRefresh = :time WHERE id = 1")
    suspend fun updateLastKeyRefresh(time: Long)

    @Query("UPDATE user_settings SET isFirstLaunch = :isFirst WHERE id = 1")
    suspend fun updateFirstLaunch(isFirst: Boolean)

    @Query("UPDATE user_settings SET passwordHash = :hash WHERE id = 1")
    suspend fun updatePasswordHash(hash: String)

    @Query("UPDATE user_settings SET biometricEnabled = :enabled WHERE id = 1")
    suspend fun updateBiometricEnabled(enabled: Boolean)
}
