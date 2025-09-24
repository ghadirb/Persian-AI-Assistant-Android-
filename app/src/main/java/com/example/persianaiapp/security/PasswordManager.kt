package com.example.persianaiapp.security

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.persianaiapp.util.CryptoUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "security_prefs")

@Singleton
class PasswordManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val APP_PASSWORD_KEY = stringPreferencesKey("app_password")
        private val PASSWORD_SALT_KEY = stringPreferencesKey("password_salt")
        private val BIOMETRIC_ENABLED_KEY = stringPreferencesKey("biometric_enabled")
    }

    suspend fun setAppPassword(password: String): Boolean {
        return try {
            val (hashedPassword, salt) = CryptoUtils.hashPassword(password)
            val saltBase64 = Base64.encodeToString(salt, Base64.DEFAULT)
            
            context.dataStore.edit { preferences ->
                preferences[APP_PASSWORD_KEY] = hashedPassword
                preferences[PASSWORD_SALT_KEY] = saltBase64
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun verifyAppPassword(password: String): Boolean {
        return try {
            val preferences = context.dataStore.data.map { it }.first()
            val storedHash = preferences[APP_PASSWORD_KEY] ?: return false
            val saltBase64 = preferences[PASSWORD_SALT_KEY] ?: return false
            val salt = Base64.decode(saltBase64, Base64.DEFAULT)
            
            CryptoUtils.verifyPassword(password, storedHash, salt)
        } catch (e: Exception) {
            false
        }
    }

    fun isPasswordSet(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[APP_PASSWORD_KEY] != null
        }
    }

    suspend fun removePassword() {
        context.dataStore.edit { preferences ->
            preferences.remove(APP_PASSWORD_KEY)
            preferences.remove(PASSWORD_SALT_KEY)
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] = enabled.toString()
        }
    }

    fun isBiometricEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY]?.toBoolean() ?: false
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        return if (verifyAppPassword(oldPassword)) {
            setAppPassword(newPassword)
        } else {
            false
        }
    }

    fun validatePasswordStrength(password: String): PasswordStrength {
        val length = password.length
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return when {
            length < 6 -> PasswordStrength.WEAK
            length >= 6 && (hasUppercase || hasLowercase) && hasDigit -> PasswordStrength.MEDIUM
            length >= 8 && hasUppercase && hasLowercase && hasDigit && hasSpecialChar -> PasswordStrength.STRONG
            else -> PasswordStrength.WEAK
        }
    }

    enum class PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}
