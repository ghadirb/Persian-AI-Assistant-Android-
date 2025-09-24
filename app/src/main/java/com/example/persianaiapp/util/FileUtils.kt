package com.example.persianaiapp.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.*

/**
 * Utility class for handling file operations
 */
object FileUtils {

    /**
     * Get the file extension from a file name or path
     */
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }

    /**
     * Get the MIME type of a file
     */
    fun getMimeType(file: File): String? {
        return getMimeType(file.name)
    }

    /**
     * Get the MIME type from a file name
     */
    fun getMimeType(fileName: String): String? {
        val extension = getFileExtension(fileName).lowercase(Locale.getDefault())
        return when (extension) {
            "txt" -> "text/plain"
            "pdf" -> "application/pdf"
            "doc", "docx" -> "application/msword"
            "xls", "xlsx" -> "application/vnd.ms-excel"
            "ppt", "pptx" -> "application/vnd.ms-powerpoint"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "mp4" -> "video/mp4"
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            else -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
    }

    /**
     * Get the file name from a content URI
     */
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }
        }
        
        if (fileName == null) {
            fileName = uri.path?.substringAfterLast('/')
        }
        
        return fileName
    }

    /**
     * Get the file size from a content URI
     */
    fun getFileSizeFromUri(context: Context, uri: Uri): Long {
        var fileSize: Long = 0
        
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        fileSize = it.getLong(sizeIndex)
                    }
                }
            }
        }
        
        if (fileSize == 0L) {
            val file = File(uri.path ?: return 0)
            if (file.exists()) {
                fileSize = file.length()
            }
        }
        
        return fileSize
    }

    /**
     * Convert a file size in bytes to a human-readable string
     */
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        
        return String.format(
            "%.1f %s",
            size / Math.pow(1024.0, digitGroups.toDouble()),
            units[digitGroups.coerceAtMost(units.size - 1)]
        )
    }

    /**
     * Create a file in the app's cache directory
     */
    fun createTempFileInCache(
        context: Context,
        prefix: String = "",
        suffix: String = ".tmp",
        directory: File? = null
    ): File {
        val cacheDir = directory ?: context.cacheDir
        return File.createTempFile(prefix, suffix, cacheDir)
    }

    /**
     * Copy a file from source to destination
     */
    @Throws(IOException::class)
    fun copyFile(source: File, destination: File) {
        source.inputStream().use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    /**
     * Copy a file from a content URI to a destination file
     */
    @Throws(IOException::class)
    fun copyFileFromUri(context: Context, uri: Uri, destination: File) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw IOException("Failed to open input stream for URI: $uri")
    }

    /**
     * Read text from a file
     */
    @Throws(IOException::class)
    fun readTextFile(file: File): String {
        return file.readText()
    }

    /**
     * Write text to a file
     */
    @Throws(IOException::class)
    fun writeTextFile(file: File, text: String, append: Boolean = false) {
        if (append) {
            file.appendText(text)
        } else {
            file.writeText(text)
        }
    }

    /**
     * Delete a directory and all its contents
     */
    fun deleteDirectory(directory: File): Boolean {
        if (directory.exists()) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        deleteDirectory(file)
                    } else {
                        file.delete()
                    }
                }
            }
        }
        return directory.delete()
    }

    /**
     * Get the real path from a content URI
     */
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return "${Environment.getExternalStorageDirectory()}/${split[1]}"
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return contentUri?.let {
                    getDataColumn(context, it, selection, selectionArgs)
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)
        }
        // File
        else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        
        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs, null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(column)
                    return it.getString(columnIndex)
                }
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Get the app's external files directory
     */
    fun getAppExternalFilesDir(context: Context, type: String? = null): File {
        return if (type != null) {
            context.getExternalFilesDir(type) ?: context.filesDir
        } else {
            context.getExternalFilesDir(null) ?: context.filesDir
        }
    }

    /**
     * Get the app's cache directory
     */
    fun getAppCacheDir(context: Context): File {
        return context.cacheDir
    }

    /**
     * Get the app's external cache directory
     */
    fun getAppExternalCacheDir(context: Context): File? {
        return context.externalCacheDir
    }

    /**
     * Check if external storage is available for read and write
     */
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * Check if external storage is available to at least read
     */
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in 
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
}
