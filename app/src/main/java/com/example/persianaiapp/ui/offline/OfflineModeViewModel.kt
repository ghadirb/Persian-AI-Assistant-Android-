package com.example.persianaiapp.ui.offline

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
class OfflineModeViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfflineModeUiState())
    val uiState: StateFlow<OfflineModeUiState> = _uiState.asStateFlow()

    init {
        checkLocalModel()
        loadAvailableModels()
    }

    private fun checkLocalModel() {
        viewModelScope.launch {
            val settings = settingsRepository.getUserSettingsSync()
            val hasModel = settings?.localModelPath?.isNotBlank() == true
            val modelName = if (hasModel) {
                settings?.localModelPath?.substringAfterLast("/") ?: "Unknown Model"
            } else ""

            _uiState.value = _uiState.value.copy(
                hasLocalModel = hasModel,
                modelName = modelName
            )
        }
    }

    private fun loadAvailableModels() {
        val models = listOf(
            OfflineModel("Llama-2-7B-Chat-GGML", 3.5f, "https://huggingface.co/TheBloke/Llama-2-7B-Chat-GGML"),
            OfflineModel("Mistral-7B-Instruct-v0.1-GGUF", 4.1f, "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.1-GGUF"),
            OfflineModel("CodeLlama-7B-Instruct-GGUF", 3.8f, "https://huggingface.co/TheBloke/CodeLlama-7B-Instruct-GGUF"),
            OfflineModel("Persian-Llama-7B-GGUF", 4.0f, "https://huggingface.co/persian-llm/Persian-Llama-7B-GGUF")
        )
        
        _uiState.value = _uiState.value.copy(availableModels = models)
    }

    fun downloadModel(modelName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                downloadingModel = modelName,
                downloadProgress = 0f,
                error = null
            )

            try {
                // Simulate download progress
                for (i in 1..100) {
                    kotlinx.coroutines.delay(50)
                    _uiState.value = _uiState.value.copy(downloadProgress = i / 100f)
                }

                // Simulate successful download
                val modelPath = "/data/data/com.example.persianaiapp/files/models/$modelName.gguf"
                settingsRepository.updateLocalModelPath(modelPath)
                
                _uiState.value = _uiState.value.copy(
                    downloadingModel = "",
                    downloadProgress = 0f,
                    hasLocalModel = true,
                    modelName = modelName,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    downloadingModel = "",
                    downloadProgress = 0f,
                    error = "خطا در دانلود مدل: ${e.message}"
                )
            }
        }
    }

    fun setCustomModelPath(path: String) {
        viewModelScope.launch {
            try {
                settingsRepository.updateLocalModelPath(path)
                val modelName = path.substringAfterLast("/")
                
                _uiState.value = _uiState.value.copy(
                    hasLocalModel = true,
                    modelName = modelName,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "خطا در تنظیم مسیر مدل: ${e.message}"
                )
            }
        }
    }

    fun setOfflineMode() {
        viewModelScope.launch {
            settingsRepository.updateOnlineMode(false)
        }
    }
}

data class OfflineModeUiState(
    val hasLocalModel: Boolean = false,
    val modelName: String = "",
    val availableModels: List<OfflineModel> = emptyList(),
    val downloadingModel: String = "",
    val downloadProgress: Float = 0f,
    val error: String? = null
)
