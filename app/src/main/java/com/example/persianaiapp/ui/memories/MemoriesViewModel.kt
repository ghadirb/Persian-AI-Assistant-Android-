package com.example.persianaiapp.ui.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.domain.model.Memory
import com.example.persianaiapp.domain.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoriesViewModel @Inject constructor(
    private val repository: MemoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MemoriesUiState())
    val state: StateFlow<MemoriesUiState> = _state.asStateFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.LoadMemories -> {
                viewModelScope.launch {
                    repository.getAllMemories().collect { memories ->
                        _state.update { it.copy(memories = memories) }
                    }
                }
            }
            is UiEvent.DeleteMemory -> {
                viewModelScope.launch {
                    repository.deleteMemory(event.memory)
                }
            }
            is UiEvent.PinMemory -> {
                viewModelScope.launch {
                    repository.pinMemory(event.id, event.isPinned)
                }
            }
            is UiEvent.SearchMemory -> {
                viewModelScope.launch {
                    repository.searchMemories(event.query).collect { memories ->
                        _state.update { it.copy(memories = memories) }
                    }
                }
            }
        }
    }

    data class MemoriesUiState(
        val memories: List<Memory> = emptyList()
    )

    sealed class UiEvent {
        object LoadMemories : UiEvent()
        data class DeleteMemory(val memory: Memory) : UiEvent()
        data class PinMemory(val id: Int, val isPinned: Boolean) : UiEvent()
        data class SearchMemory(val query: String) : UiEvent()
    }
}
