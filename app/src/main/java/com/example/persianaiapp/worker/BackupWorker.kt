package com.example.persianaiapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.persianaiapp.backup.BackupManager
import com.example.persianaiapp.data.repository.ISettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupManager: BackupManager,
    private val settingsRepository: ISettingsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting scheduled backup")
            
            // Check if auto backup is enabled
            val settings = settingsRepository.getUserSettingsSync()
            if (settings?.autoBackupEnabled != true) {
                Timber.d("Auto backup is disabled, skipping")
                return Result.success()
            }
            
            // Perform local backup
            val localBackupFile = backupManager.createLocalBackup()
            Timber.d("Local backup created: ${localBackupFile.absolutePath}")
            
            // Perform encrypted backup if enabled
            if (settings.encryptedBackupEnabled) {
                val encryptedBackupFile = backupManager.createEncryptedBackup("auto_backup_password")
                Timber.d("Encrypted backup created: ${encryptedBackupFile.absolutePath}")
            }
            
            // Upload to Google Drive if enabled
            if (settings.googleDriveBackupEnabled) {
                val fileToUpload = if (settings.encryptedBackupEnabled) {
                    backupManager.createEncryptedBackup("auto_backup_password")
                } else {
                    localBackupFile
                }
                val driveFileId = backupManager.uploadToGoogleDrive(fileToUpload)
                if (driveFileId != null) {
                    Timber.d("Backup uploaded to Google Drive: $driveFileId")
                } else {
                    Timber.e("Google Drive backup failed")
                    return Result.retry()
                }
            }
            
            Timber.d("Scheduled backup completed successfully")
            Result.success()
            
        } catch (e: Exception) {
            Timber.e(e, "Backup worker failed")
            Result.retry()
        }
    }
}
