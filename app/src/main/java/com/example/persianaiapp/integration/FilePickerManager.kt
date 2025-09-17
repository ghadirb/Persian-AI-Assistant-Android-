package com.example.persianaiapp.integration

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.persianaiapp.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilePickerManager @Inject constructor() {

    fun createModelFilePicker(
        fragment: Fragment,
        onFileSelected: (Uri) -> Unit,
        onError: (String) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                try {
                    // Validate file extension
                    val fileName = getFileName(fragment.requireContext(), uri)
                    if (isValidModelFile(fileName)) {
                        onFileSelected(uri)
                    } else {
                        onError(fragment.getString(R.string.file_invalid))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error processing selected file")
                    onError(fragment.getString(R.string.file_not_found))
                }
            }
        }
    }

    fun createBackupFilePicker(
        fragment: Fragment,
        onFileSelected: (Uri) -> Unit,
        onError: (String) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                try {
                    val fileName = getFileName(fragment.requireContext(), uri)
                    if (isValidBackupFile(fileName)) {
                        onFileSelected(uri)
                    } else {
                        onError(fragment.getString(R.string.file_invalid))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error processing backup file")
                    onError(fragment.getString(R.string.file_not_found))
                }
            }
        }
    }

    fun openModelFilePicker(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(arrayOf(
            "application/octet-stream", // .gguf files
            "application/x-onnx", // .onnx files  
            "application/x-tensorflow", // .tflite files
            "*/*" // Allow all files as fallback
        ))
    }

    fun openBackupFilePicker(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(arrayOf(
            "application/json",
            "application/zip",
            "application/octet-stream",
            "*/*"
        ))
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        return try {
            val cursor = context.contentResolver.query(
                uri, null, null, null, null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        it.getString(nameIndex)
                    } else null
                } else null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting file name")
            null
        }
    }

    private fun isValidModelFile(fileName: String?): Boolean {
        if (fileName == null) return false
        
        val validExtensions = listOf(".gguf", ".onnx", ".tflite", ".bin", ".safetensors")
        return validExtensions.any { fileName.lowercase().endsWith(it) }
    }

    private fun isValidBackupFile(fileName: String?): Boolean {
        if (fileName == null) return false
        
        val validExtensions = listOf(".json", ".zip", ".backup")
        return validExtensions.any { fileName.lowercase().endsWith(it) }
    }

    fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            val cursor = context.contentResolver.query(
                uri, null, null, null, null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
                    if (sizeIndex >= 0) {
                        it.getLong(sizeIndex)
                    } else 0L
                } else 0L
            } ?: 0L
        } catch (e: Exception) {
            Timber.e(e, "Error getting file size")
            0L
        }
    }

    fun isFileTooLarge(context: Context, uri: Uri, maxSizeMB: Long = 2048): Boolean {
        val fileSizeBytes = getFileSize(context, uri)
        val maxSizeBytes = maxSizeMB * 1024 * 1024
        return fileSizeBytes > maxSizeBytes
    }
}
