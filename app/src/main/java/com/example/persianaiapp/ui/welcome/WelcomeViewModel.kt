package com.example.persianaiapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.data.repository.ISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val settings = settingsRepository.getUserSettingsSync()
            _uiState.value = _uiState.value.copy(
                isFirstLaunch = settings?.isFirstLaunch ?: true,
                isLoading = false
            )
        }
    }

    fun markFirstLaunchComplete() {
        viewModelScope.launch {
            settingsRepository.updateFirstLaunch(false)
        }
    }
}

data class WelcomeUiState(
    val isLoading: Boolean = true,
    val isFirstLaunch: Boolean = true,
    val error: String? = null
)
