package com.example.persianaiapp.backup

import android.content.Context
import com.example.persianaiapp.domain.repository.ChatRepository
import com.example.persianaiapp.domain.repository.MemoryRepository
import com.example.persianaiapp.data.repository.ISettingsRepository
import com.example.persianaiapp.util.CryptoUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val context: Context,
    private val chatRepository: ChatRepository,
    private val memoryRepository: MemoryRepository,
    private val settingsRepository: ISettingsRepository
) {
    
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())

    suspend fun createLocalBackup(): File = withContext(Dispatchers.IO) {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }

        val timestamp = dateFormat.format(Date())
        val backupFile = File(backupDir, "backup_$timestamp.json")

        val backupData = BackupData(
            messages = chatRepository.getRecentMessages(1000),
            memories = memoryRepository.getAllMemories().first(),
            settings = settingsRepository.getUserSettingsSync(),
            timestamp = System.currentTimeMillis(),
            version = "1.0"
        )

        val backupJson = gson.toJson(backupData)
        backupFile.writeText(backupJson)

        backupFile
    }

    suspend fun createEncryptedBackup(password: String): File = withContext(Dispatchers.IO) {
        val localBackup = createLocalBackup()
        val backupContent = localBackup.readText()
        
        val encryptedContent = CryptoUtils.encryptData(backupContent, password)
            ?: throw Exception("خطا در رمزگذاری بک‌آپ")

        val encryptedFile = File(localBackup.parent, "${localBackup.nameWithoutExtension}_encrypted.enc")
        encryptedFile.writeText(encryptedContent)

        localBackup.delete()
        encryptedFile
    }

    suspend fun restoreFromBackup(backupFile: File, password: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            val backupContent = if (password != null && backupFile.extension == "enc") {
                // Decrypt backup
                val encryptedContent = backupFile.readText()
                CryptoUtils.decryptApiKeys(encryptedContent, password)
                    ?: throw Exception("خطا در رمزگشایی بک‌آپ")
            } else {
                backupFile.readText()
            }

            val backupData = gson.fromJson(backupContent, BackupData::class.java)

            // Restore data
            backupData.messages.forEach { message ->
                chatRepository.insertMessage(message)
            }

            backupData.memories.forEach { memory ->
                memoryRepository.insertMemory(memory)
            }

            backupData.settings?.let { settings ->
                settingsRepository.saveUserSettings(settings)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadToGoogleDrive(backupFile: File): String? = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext null

            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = backupFile.name
                parents = listOf(getOrCreateBackupFolder(driveService))
            }

            val mediaContent = com.google.api.client.http.FileContent("application/json", backupFile)

            val file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()

            file.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadFromGoogleDrive(fileId: String): File? = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext null

            val outputStream = ByteArrayOutputStream()
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)

            val downloadedFile = File(context.filesDir, "downloaded_backup_${System.currentTimeMillis()}.json")
            downloadedFile.writeBytes(outputStream.toByteArray())

            downloadedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun listGoogleDriveBackups(): List<DriveBackupInfo> = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext emptyList()
            val folderId = getOrCreateBackupFolder(driveService)

            val result = driveService.files().list()
                .setQ("parents in '$folderId' and trashed=false")
                .setFields("files(id, name, createdTime, size)")
                .execute()

            result.files.map { file ->
                DriveBackupInfo(
                    id = file.id,
                    name = file.name,
                    createdTime = file.createdTime?.value ?: 0L,
                    size = file.getSize() ?: 0L
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getDriveService(): Drive? {
        return try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account == null) {
                // Request sign in
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                    .build()
                return null
            }

            val credential = com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
                .usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
            credential.selectedAccount = account.account

            Drive.Builder(
                com.google.api.client.http.javanet.NetHttpTransport(),
                com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("Persian AI Assistant").build()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getOrCreateBackupFolder(driveService: Drive): String {
        // Check if folder exists
        val result = driveService.files().list()
            .setQ("name='PersianAI_Backups' and mimeType='application/vnd.google-apps.folder' and trashed=false")
            .execute()

        return if (result.files.isNotEmpty()) {
            result.files[0].id
        } else {
            // Create folder
            val folderMetadata = com.google.api.services.drive.model.File().apply {
                name = "PersianAI_Backups"
                mimeType = "application/vnd.google-apps.folder"
            }
            
            val folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute()
            
            folder.id
        }
    }

    suspend fun scheduleAutoBackup() {
        // TODO: Implement WorkManager for scheduled backups
        val settings = settingsRepository.getUserSettingsSync()
        if (settings?.autoBackupEnabled == true) {
            // Schedule backup based on backupInterval
        }
    }

    data class BackupData(
        val messages: List<com.example.persianaiapp.data.local.entity.ChatMessage>,
        val memories: List<com.example.persianaiapp.data.local.entity.Memory>,
        val settings: com.example.persianaiapp.data.local.entity.UserSettings?,
        val timestamp: Long,
        val version: String
    )

    data class DriveBackupInfo(
        val id: String,
        val name: String,
        val createdTime: Long,
        val size: Long
    )
}
