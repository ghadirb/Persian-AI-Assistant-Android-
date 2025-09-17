package com.example.persianaiapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.data.local.entity.Memory
import com.example.persianaiapp.domain.repository.MemoryRepository
import com.example.persianaiapp.data.repository.ISettingsRepository
import com.example.persianaiapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        loadInitialData()
        observeSettings()
    }

    private fun loadInitialData() {
        loadRecentMemories()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _uiState.update { currentState ->
                    currentState.copy(
                        mode = if (settings.isOfflineMode) Mode.OFFLINE else Mode.ONLINE,
                        isConnected = settings.isConnected
                    )
                }
            }
        }
    }

    private fun loadRecentMemories() {
        viewModelScope.launch {
            memoryRepository.getRecentMemories(5).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(recentMemories = result.data) }
                    }
                    is Result.Error -> {
                        _events.emit(UiEvent.ShowMessage(result.message ?: "Error loading memories"))
                    }
                }
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadRecentMemories()
        _uiState.update { it.copy(isLoading = false) }
    }

    fun setMode(mode: Mode) {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(isOfflineMode = mode == Mode.OFFLINE) }
        }
    }

    fun onVoiceInputClick() {
        viewModelScope.launch {
            _events.emit(UiEvent.RequestVoiceInput)
        }
    }

    data class HomeUiState(
        val isLoading: Boolean = false,
        val mode: Mode = Mode.ONLINE,
        val isConnected: Boolean = false,
        val connectionStatus: String = "Connected",
        val recentMemories: List<Memory> = emptyList()
    )

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        object RequestVoiceInput : UiEvent()
    }

    enum class Mode {
        ONLINE,
        OFFLINE
    }
}
