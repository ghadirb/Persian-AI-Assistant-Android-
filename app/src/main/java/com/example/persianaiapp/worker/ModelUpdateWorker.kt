package com.example.persianaiapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.persianaiapp.model.LocalModelManager
import com.example.persianaiapp.data.repository.ISettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class ModelUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localModelManager: LocalModelManager,
    private val settingsRepository: ISettingsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting model update check")
            
            // Check if auto model updates are enabled
            val settings = settingsRepository.getUserSettingsSync()
            if (settings?.autoModelUpdateEnabled != true) {
                Timber.d("Auto model updates disabled, skipping")
                return Result.success()
            }
            
            // Check for available model updates
            val availableModels = localModelManager.getAvailableModels()
            val installedModels = localModelManager.getInstalledModels()
            
            var updatesFound = false
            
            for (availableModel in availableModels) {
                val installedModel = installedModels.find { it.id == availableModel.id }
                if (installedModel != null && availableModel.version > installedModel.version) {
                    Timber.d("Update available for model: ${availableModel.name}")
                    
                    // Download updated model if auto-download is enabled
                    if (settings.autoDownloadModelUpdates) {
                        val downloadResult = localModelManager.downloadModel(availableModel.id)
                        if (downloadResult) {
                            Timber.d("Successfully updated model: ${availableModel.name}")
                            updatesFound = true
                        } else {
                            Timber.e("Failed to update model: ${availableModel.name}")
                        }
                    } else {
                        // Just mark that updates are available
                        updatesFound = true
                    }
                }
            }
            
            if (updatesFound) {
                Timber.d("Model updates completed or available")
            } else {
                Timber.d("No model updates available")
            }
            
            Result.success()
            
        } catch (e: Exception) {
            Timber.e(e, "Model update worker failed")
            Result.retry()
        }
    }
}
