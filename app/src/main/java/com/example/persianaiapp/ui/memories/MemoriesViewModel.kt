package com.example.persianaiapp.ui.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.data.local.entity.Memory
import com.example.persianaiapp.domain.repository.MemoryRepository
import com.example.persianaiapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoriesViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MemoriesUiState>(MemoriesUiState.Loading)
    val uiState: StateFlow<MemoriesUiState> = _uiState

    private val _events = MutableStateFlow<UiEvent?>(null)
    val events: StateFlow<UiEvent?> = _events

    init {
        loadMemories()
    }

    fun loadMemories() {
        viewModelScope.launch {
            _uiState.value = MemoriesUiState.Loading
            
            memoryRepository.getAllMemories()
                .catch { e ->
                    _uiState.value = MemoriesUiState.Error("Error loading memories: ${e.message}")
                }
                .collectLatest { memories ->
                    if (memories.isEmpty()) {
                        _uiState.value = MemoriesUiState.Empty
                    } else {
                        _uiState.value = MemoriesUiState.Success(memories)
                    }
                }
        }
    }

    fun deleteMemory(memory: Memory) {
        viewModelScope.launch {
            when (val result = memoryRepository.deleteMemory(memory)) {
                is Result.Success -> {
                    _events.value = UiEvent.ShowMessage("Memory deleted")
                }
                is Result.Error -> {
                    _events.value = UiEvent.ShowMessage("Failed to delete memory: ${result.message}")
                }
                else -> {}
            }
            // Clear the event after it's been handled
            _events.value = null
        }
    }

    fun togglePinMemory(memory: Memory) {
        viewModelScope.launch {
            val isPinned = !memory.isPinned
            when (val result = memoryRepository.togglePinMemory(memory.id, isPinned)) {
                is Result.Success -> {
                    val message = if (isPinned) "Memory pinned" else "Memory unpinned"
                    _events.value = UiEvent.ShowMessage(message)
                }
                is Result.Error -> {
                    _events.value = UiEvent.ShowMessage("Failed to update memory: ${result.message}")
                }
                else -> {}
            }
            // Clear the event after it's been handled
            _events.value = null
        }
    }

    fun searchMemories(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadMemories()
                return@launch
            }

            _uiState.value = MemoriesUiState.Loading
            
            memoryRepository.searchMemories(query)
                .catch { e ->
                    _uiState.value = MemoriesUiState.Error("Error searching memories: ${e.message}")
                }
                .collectLatest { memories ->
                    if (memories.isEmpty()) {
                        _uiState.value = MemoriesUiState.EmptySearch(query)
                    } else {
                        _uiState.value = MemoriesUiState.Success(memories)
                    }
                }
        }
    }

    sealed class MemoriesUiState {
        object Loading : MemoriesUiState()
        object Empty : MemoriesUiState()
        data class EmptySearch(val query: String) : MemoriesUiState()
        data class Success(val memories: List<Memory>) : MemoriesUiState()
        data class Error(val message: String) : MemoriesUiState()
    }

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class NavigateToMemory(val memoryId: Long) : UiEvent()
        object NavigateToAddMemory : UiEvent()
    }
}
