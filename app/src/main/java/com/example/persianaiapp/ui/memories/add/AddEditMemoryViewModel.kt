package com.example.persianaiapp.ui.memories.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.data.model.Memory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMemoryViewModel @Inject constructor(): ViewModel() {

    sealed class AddEditMemoryUiState {
        object Loading : AddEditMemoryUiState()
        data class Editing(val memory: Memory) : AddEditMemoryUiState()
        object Saving : AddEditMemoryUiState()
        data class Error(val message: String) : AddEditMemoryUiState()
    }

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class MemorySaved(val memoryId: Long) : UiEvent()
        object MemoryUpdated : UiEvent()
    }

    private val _uiState = MutableStateFlow<AddEditMemoryUiState>(AddEditMemoryUiState.Editing(Memory()))
    val uiState: StateFlow<AddEditMemoryUiState> = _uiState

    private val _events = MutableStateFlow<UiEvent?>(null)
    val events: StateFlow<UiEvent?> = _events

    fun updateTitle(title: String) {
        val current = (_uiState.value as? AddEditMemoryUiState.Editing)?.memory ?: Memory()
        _uiState.value = AddEditMemoryUiState.Editing(current.copy(title = title))
    }

    fun updateContent(content: String) {
        val current = (_uiState.value as? AddEditMemoryUiState.Editing)?.memory ?: Memory()
        _uiState.value = AddEditMemoryUiState.Editing(current.copy(content = content))
    }

    fun updateTags(tags: List<String>) {
        val current = (_uiState.value as? AddEditMemoryUiState.Editing)?.memory ?: Memory()
        _uiState.value = AddEditMemoryUiState.Editing(current.copy(tags = tags))
    }

    fun togglePinned() {
        val current = (_uiState.value as? AddEditMemoryUiState.Editing)?.memory ?: Memory()
        _uiState.value = AddEditMemoryUiState.Editing(current.copy(isPinned = !current.isPinned))
    }

    fun saveMemory() {
        viewModelScope.launch {
            // TODO integrate repository
            _events.value = UiEvent.MemorySaved(1L)
            _events.value = null
        }
    }
}

package com.example.persianaiapp.ui.memories.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.data.local.entity.Memory
import com.example.persianaiapp.domain.repository.MemoryRepository
import com.example.persianaiapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditMemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val memoryId: Long = savedStateHandle["memoryId"] ?: -1L
    
    private val _uiState = MutableStateFlow<AddEditMemoryUiState>(AddEditMemoryUiState.Loading)
    val uiState: StateFlow<AddEditMemoryUiState> = _uiState

    private val _events = MutableStateFlow<UiEvent?>(null)
    val events: StateFlow<UiEvent?> = _events

    init {
        if (memoryId != -1L) {
            loadMemory()
        } else {
            _uiState.value = AddEditMemoryUiState.Editing(
                Memory(
                    id = 0,
                    title = "",
                    content = "",
                    tags = emptyList(),
                    isPinned = false,
                    createdAt = Date(),
                    updatedAt = Date()
                )
            )
        }
    }

    private fun loadMemory() {
        viewModelScope.launch {
            _uiState.value = AddEditMemoryUiState.Loading
            
            when (val result = memoryRepository.getMemoryById(memoryId)) {
                is Result.Success -> {
                    _uiState.value = AddEditMemoryUiState.Editing(result.data)
                }
                is Result.Error -> {
                    _uiState.value = AddEditMemoryUiState.Error("Error loading memory: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun updateTitle(title: String) {
        val currentState = _uiState.value
        if (currentState is AddEditMemoryUiState.Editing) {
            _uiState.value = currentState.copy(
                memory = currentState.memory.copy(
                    title = title,
                    updatedAt = Date()
                )
            )
        }
    }

    fun updateContent(content: String) {
        val currentState = _uiState.value
        if (currentState is AddEditMemoryUiState.Editing) {
            _uiState.value = currentState.copy(
                memory = currentState.memory.copy(
                    content = content,
                    updatedAt = Date()
                )
            )
        }
    }

    fun updateTags(tags: List<String>) {
        val currentState = _uiState.value
        if (currentState is AddEditMemoryUiState.Editing) {
            _uiState.value = currentState.copy(
                memory = currentState.memory.copy(
                    tags = tags,
                    updatedAt = Date()
                )
            )
        }
    }

    fun togglePinned() {
        val currentState = _uiState.value
        if (currentState is AddEditMemoryUiState.Editing) {
            _uiState.value = currentState.copy(
                memory = currentState.memory.copy(
                    isPinned = !currentState.memory.isPinned,
                    updatedAt = Date()
                )
            )
        }
    }

    fun saveMemory() {
        val currentState = _uiState.value
        if (currentState !is AddEditMemoryUiState.Editing) return
        
        val memory = currentState.memory
        
        if (memory.title.isBlank()) {
            _events.value = UiEvent.ShowMessage("Please enter a title")
            return
        }
        
        if (memory.content.isBlank()) {
            _events.value = UiEvent.ShowMessage("Please enter some content")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = AddEditMemoryUiState.Saving
            
            val result = if (memory.id == 0L) {
                // New memory
                memoryRepository.saveMemory(memory)
            } else {
                // Update existing memory
                memoryRepository.updateMemory(memory)
            }
            
            when (result) {
                is Result.Success -> {
                    _events.value = if (memory.id == 0L) {
                        UiEvent.MemorySaved(result.data)
                    } else {
                        UiEvent.MemoryUpdated
                    }
                }
                is Result.Error -> {
                    _events.value = UiEvent.ShowMessage("Error saving memory: ${result.message}")
                    _uiState.value = currentState
                }
                else -> {}
            }
        }
    }

    sealed class AddEditMemoryUiState {
        object Loading : AddEditMemoryUiState()
        data class Editing(val memory: Memory) : AddEditMemoryUiState()
        object Saving : AddEditMemoryUiState()
        data class Error(val message: String) : AddEditMemoryUiState()
    }

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class MemorySaved(val memoryId: Long) : UiEvent()
        object MemoryUpdated : UiEvent()
    }
}
