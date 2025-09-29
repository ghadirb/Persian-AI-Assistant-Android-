package com.example.persianaiapp.ui.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.security.EncryptedKeysManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class KeySetupViewModel @Inject constructor(
    private val encryptedKeysManager: EncryptedKeysManager
) : ViewModel() {

    data class KeySetupUiState(
        val isLoading: Boolean = false,
        val isDecrypting: Boolean = false,
        val keysValid: Boolean = false,
        val showDecryptionDialog: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(KeySetupUiState())
    val uiState: StateFlow<KeySetupUiState> = _uiState.asStateFlow()

    init {
        checkExistingKeys()
    }

    private fun checkExistingKeys() {
        _uiState.value = _uiState.value.copy(
            keysValid = encryptedKeysManager.areKeysValid()
        )
    }

    fun downloadAndDecryptKeys() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = withTimeoutOrNull(15000) { // 15 second timeout
                    encryptedKeysManager.downloadAndDecryptKeys()
                }
                
                if (result != null) {
                    result.fold(
                        onSuccess = { keys ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                keysValid = true,
                                errorMessage = null
                            )
                            Timber.d("Keys downloaded and decrypted successfully")
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                keysValid = false,
                                errorMessage = error.message
                            )
                            Timber.e(error, "Failed to download and decrypt keys")
                        }
                    )
                } else {
                    showError("Timeout occurred while downloading and decrypting keys")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    keysValid = false,
                    errorMessage = e.message
                )
                Timber.e(e, "Unexpected error during key setup")
            }
        }
    }

    fun decryptKeys(password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDecrypting = true,
                errorMessage = null
            )

            try {
                val result = encryptedKeysManager.downloadAndDecryptKeys(password)
                result.fold(
                    onSuccess = { keys ->
                        _uiState.value = _uiState.value.copy(
                            isDecrypting = false,
                            keysValid = true,
                            showDecryptionDialog = false,
                            errorMessage = null
                        )
                        Timber.d("Keys decrypted successfully with custom password")
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isDecrypting = false,
                            keysValid = false,
                            errorMessage = if (error.message?.contains("password") == true || 
                                              error.message?.contains("decrypt") == true) {
                                "رمز عبور اشتباه است"
                            } else {
                                error.message
                            }
                        )
                        Timber.e(error, "Failed to decrypt keys with custom password")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDecrypting = false,
                    keysValid = false,
                    errorMessage = "خطای غیرمنتظره: ${e.message}"
                )
                Timber.e(e, "Unexpected error during key decryption")
            }
        }
    }

    fun showDecryptionDialog() {
        _uiState.value = _uiState.value.copy(
            showDecryptionDialog = true,
            errorMessage = null
        )
    }

    fun hideDecryptionDialog() {
        _uiState.value = _uiState.value.copy(
            showDecryptionDialog = false,
            errorMessage = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }
}
