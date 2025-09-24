package com.example.persianaiapp.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.integration.FilePickerManager
import com.example.persianaiapp.model.OfflineModelDownloader
import com.example.persianaiapp.model.OfflineModelInference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ModelManagementViewModel @Inject constructor(
    private val offlineModelDownloader: OfflineModelDownloader,
    private val offlineModelInference: OfflineModelInference,
    private val filePickerManager: FilePickerManager
) : ViewModel() {

    data class ModelManagementUiState(
        val availableModels: List<OfflineModelDownloader.AvailableModel> = emptyList(),
        val installedModels: List<File> = emptyList(),
        val currentModelName: String? = null,
        val isModelLoaded: Boolean = false,
        val isLoading: Boolean = false,
        val downloadingModels: Set<String> = emptySet(),
        val downloadProgress: Map<String, OfflineModelDownloader.DownloadProgress> = emptyMap(),
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(ModelManagementUiState())
    val uiState: StateFlow<ModelManagementUiState> = _uiState.asStateFlow()

    fun loadModels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val availableModels = offlineModelDownloader.getAvailableModels()
                val installedModels = offlineModelDownloader.getInstalledModels()
                val currentModel = offlineModelInference.getCurrentModelPath()

                _uiState.value = _uiState.value.copy(
                    availableModels = availableModels,
                    installedModels = installedModels,
                    currentModelName = currentModel?.let { File(it).name },
                    isModelLoaded = currentModel != null,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                Timber.e(e, "Error loading models")
            }
        }
    }

    fun downloadModel(model: OfflineModelDownloader.AvailableModel) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                downloadingModels = _uiState.value.downloadingModels + model.name
            )

            offlineModelDownloader.downloadModel(model).collect { progress ->
                _uiState.value = _uiState.value.copy(
                    downloadProgress = _uiState.value.downloadProgress + (model.name to progress)
                )

                if (progress.isComplete || progress.error != null) {
                    _uiState.value = _uiState.value.copy(
                        downloadingModels = _uiState.value.downloadingModels - model.name
                    )

                    if (progress.isComplete) {
                        // Refresh installed models list
                        loadModels()
                    }
                }
            }
        }
    }

    fun selectModel(modelName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val modelsDir = offlineModelDownloader.getModelsDirectory()
                val modelFile = File(modelsDir, modelName)

                if (modelFile.exists()) {
                    val success = offlineModelInference.loadModel(modelFile.absolutePath)
                    if (success) {
                        _uiState.value = _uiState.value.copy(
                            currentModelName = modelName,
                            isModelLoaded = true,
                            isLoading = false,
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "خطا در بارگذاری مدل"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "فایل مدل پیدا نشد"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                Timber.e(e, "Error selecting model: $modelName")
            }
        }
    }

    fun deleteModel(modelName: String) {
        viewModelScope.launch {
            try {
                val success = offlineModelDownloader.deleteModel(modelName)
                if (success) {
                    // If deleted model was the current one, clear it
                    if (_uiState.value.currentModelName == modelName) {
                        _uiState.value = _uiState.value.copy(
                            currentModelName = null,
                            isModelLoaded = false
                        )
                    }
                    // Refresh models list
                    loadModels()
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "خطا در حذف مدل"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
                Timber.e(e, "Error deleting model: $modelName")
            }
        }
    }

    fun selectModelFile() {
        // This would typically trigger a file picker
        // For now, we'll just log the action
        Timber.d("Model file selection requested")
        _uiState.value = _uiState.value.copy(
            errorMessage = "انتخاب فایل مدل به زودی اضافه خواهد شد"
        )
    }

    fun showModelSelection() {
        // This could trigger a dialog or navigation
        Timber.d("Model selection dialog requested")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
