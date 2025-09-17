package com.example.persianaiapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.backup.BackupManager
import com.example.persianaiapp.data.repository.ISettingsRepository
import com.example.persianaiapp.model.LocalModelManager
import com.example.persianaiapp.security.BiometricAuthManager
import com.example.persianaiapp.security.PasswordManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository,
    private val passwordManager: PasswordManager,
    private val biometricAuthManager: BiometricAuthManager,
    private val backupManager: BackupManager,
    private val localModelManager: LocalModelManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.getUserSettings().filterNotNull(),
                passwordManager.isPasswordSet(),
                passwordManager.isBiometricEnabled()
            ) { settings, passwordSet, biometricEnabled ->
                _uiState.value = _uiState.value.copy(
                    selectedLanguage = settings.language ?: "فارسی",
                    selectedTheme = settings.theme ?: "سیستم",
                    voiceActivationEnabled = settings.voiceActivationEnabled,
                    voiceFeedbackEnabled = settings.voiceFeedbackEnabled,
                    biometricEnabled = biometricEnabled,
                    passwordSet = passwordSet,
                    autoBackupEnabled = settings.autoBackupEnabled,
                    autoModelUpdateEnabled = settings.autoModelUpdateEnabled,
                    appVersion = "1.0.0"
                )
            }.collect()

            // Load installed models count
            val installedModels = localModelManager.getInstalledModels()
            _uiState.value = _uiState.value.copy(
                installedModelsCount = installedModels.size
            )
        }
    }

    fun showLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = true)
    }

    fun hideLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = false)
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(language)
            hideLanguageDialog()
        }
    }

    fun showThemeDialog() {
        _uiState.value = _uiState.value.copy(showThemeDialog = true)
    }

    fun hideThemeDialog() {
        _uiState.value = _uiState.value.copy(showThemeDialog = false)
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme)
            hideThemeDialog()
        }
    }

    fun setVoiceActivation(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVoiceActivationEnabled(enabled)
        }
    }

    fun setVoiceFeedback(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVoiceFeedbackEnabled(enabled)
        }
    }

    fun setBiometricAuth(enabled: Boolean) {
        viewModelScope.launch {
            passwordManager.setBiometricEnabled(enabled)
        }
    }

    fun showPasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = true)
    }

    fun hidePasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = false)
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            val success = passwordManager.setAppPassword(password)
            if (success) {
                hidePasswordDialog()
            }
        }
    }

    fun removePassword() {
        viewModelScope.launch {
            passwordManager.removePassword()
            hidePasswordDialog()
        }
    }

    fun setAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutoBackupEnabled(enabled)
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            backupManager.createLocalBackup()
        }
    }

    fun showRestoreDialog() {
        _uiState.value = _uiState.value.copy(showRestoreDialog = true)
    }

    fun hideRestoreDialog() {
        _uiState.value = _uiState.value.copy(showRestoreDialog = false)
    }

    fun showModelManager() {
        // Navigate to model manager screen
    }

    fun setAutoModelUpdate(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutoModelUpdateEnabled(enabled)
        }
    }

    fun showAboutDialog() {
        _uiState.value = _uiState.value.copy(showAboutDialog = true)
    }

    fun hideAboutDialog() {
        _uiState.value = _uiState.value.copy(showAboutDialog = false)
    }

    fun openPrivacyPolicy() {
        // Open privacy policy URL
    }
}

data class SettingsUiState(
    val selectedLanguage: String = "فارسی",
    val selectedTheme: String = "سیستم",
    val voiceActivationEnabled: Boolean = false,
    val voiceFeedbackEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val passwordSet: Boolean = false,
    val autoBackupEnabled: Boolean = false,
    val autoModelUpdateEnabled: Boolean = false,
    val installedModelsCount: Int = 0,
    val appVersion: String = "1.0.0",
    val showLanguageDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showRestoreDialog: Boolean = false,
    val showAboutDialog: Boolean = false
)
