package com.example.persianaiapp.ui.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.data.remote.GoogleDriveService
import com.example.persianaiapp.data.repository.ISettingsRepository
import com.example.persianaiapp.ai.AIModelManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineModeViewModel @Inject constructor(
    private val googleDriveService: GoogleDriveService,
    private val settingsRepository: ISettingsRepository,
    private val aiModelManager: AIModelManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnlineModeUiState())
    val uiState: StateFlow<OnlineModeUiState> = _uiState.asStateFlow()

    fun authenticateAndLoadKeys(password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                // Download and decrypt API keys
                val result = googleDriveService.downloadAndDecryptKeys(password)
                
                result.fold(
                    onSuccess = { apiKeys ->
                        if (googleDriveService.validateApiKeys(apiKeys)) {
                            // Save encrypted keys to settings
                            val keysJson = apiKeys.entries.joinToString("\n") { "${it.key}=${it.value}" }
                            settingsRepository.updateEncryptedApiKeys(keysJson)
                            
                            // Update online mode
                            settingsRepository.updateOnlineMode(true)
                            
                            // Set best available model
                            val bestModel = googleDriveService.getBestAvailableModel(apiKeys)
                            settingsRepository.updateSelectedModel(bestModel)
                            
                            // Update last key refresh time
                            settingsRepository.updateLastKeyRefresh(System.currentTimeMillis())
                            
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isAuthenticated = true,
                                activeModel = bestModel,
                                shouldNavigateToChat = true,
                                error = null
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "کلیدهای API معتبر یافت نشد"
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = when {
                                exception.message?.contains("decrypt") == true -> "رمز عبور اشتباه است"
                                exception.message?.contains("download") == true -> "خطا در دانلود کلیدها"
                                else -> "خطا در اتصال: ${exception.message}"
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "خطای غیرمنتظره: ${e.message}"
                )
            }
        }
    }

    fun refreshKeys(password: String) {
        authenticateAndLoadKeys(password)
    }

    fun clearNavigationFlag() {
        _uiState.value = _uiState.value.copy(shouldNavigateToChat = false)
    }
}

data class OnlineModeUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val activeModel: String = "",
    val shouldNavigateToChat: Boolean = false,
    val error: String? = null
)
