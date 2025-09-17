package com.example.persianaiapp.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Permission utility class that handles runtime permissions in a type-safe way.
 */
object PermissionUtils {

    // Common permission requests
    object Permissions {
        // Audio permissions
        val RECORD_AUDIO = arrayOf(Manifest.permission.RECORD_AUDIO)
        
        // Storage permissions (for API < 33)
        val STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        // Media permissions (for API >= 33)
        val MEDIA = arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
        
        // Location permissions
        val LOCATION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        // Contacts permissions
        val CONTACTS = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
        
        // Calendar permissions
        val CALENDAR = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
        
        // Camera permission
        val CAMERA = arrayOf(Manifest.permission.CAMERA)
    }
    
    /**
     * Check if all the given permissions are granted
     */
    fun Context.hasPermissions(vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    
    /**
     * Check if the app should show a rationale for any of the permissions
     */
    fun FragmentActivity.shouldShowRationale(vararg permissions: String): Boolean {
        return permissions.any {
            shouldShowRequestPermissionRationale(it)
        }
    }
    
    /**
     * Check if the app should show a rationale for any of the permissions
     */
    fun Fragment.shouldShowRationale(vararg permissions: String): Boolean {
        return permissions.any {
            shouldShowRequestPermissionRationale(it)
        }
    }
    
    /**
     * Request permissions and handle the result
     */
    fun Fragment.requestPermissions(
        permissions: Array<String>,
        onPermissionsGranted: () -> Unit = {},
        onPermissionsDenied: (List<String>) -> Unit = {},
        onShowRationale: (List<String>) -> Unit = {}
    ) {
        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsMap ->
            val deniedPermissions = permissionsMap.filter { !it.value }.map { it.key }
            val grantedPermissions = permissionsMap.filter { it.value }.map { it.key }
            
            if (deniedPermissions.isEmpty()) {
                // All permissions granted
                onPermissionsGranted()
            } else {
                // Some permissions denied
                if (shouldShowRationale(*deniedPermissions.toTypedArray())) {
                    // Show rationale for the denied permissions
                    onShowRationale(deniedPermissions)
                } else {
                    // Permissions denied with "Don't ask again"
                    onPermissionsDenied(deniedPermissions)
                }
            }
        }
        
        // Check if we already have the permissions
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            onPermissionsGranted()
        } else {
            // Request the permissions
            launcher.launch(permissionsToRequest)
        }
    }
    
    /**
     * Request permissions and handle the result
     */
    fun Activity.requestPermissions(
        permissions: Array<String>,
        onPermissionsGranted: () -> Unit = {},
        onPermissionsDenied: (List<String>) -> Unit = {},
        onShowRationale: (List<String>) -> Unit = {}
    ) {
        val launcher = (this as? FragmentActivity)?.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsMap ->
            val deniedPermissions = permissionsMap.filter { !it.value }.map { it.key }
            val grantedPermissions = permissionsMap.filter { it.value }.map { it.key }
            
            if (deniedPermissions.isEmpty()) {
                // All permissions granted
                onPermissionsGranted()
            } else {
                // Some permissions denied
                if (shouldShowRationale(*deniedPermissions.toTypedArray())) {
                    // Show rationale for the denied permissions
                    onShowRationale(deniedPermissions)
                } else {
                    // Permissions denied with "Don't ask again"
                    onPermissionsDenied(deniedPermissions)
                }
            }
        }
        
        // Check if we already have the permissions
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            onPermissionsGranted()
        } else if (launcher != null) {
            // Request the permissions
            launcher.launch(permissionsToRequest)
        } else {
            // Fallback to the old permission system
            onPermissionsDenied(permissionsToRequest.toList())
        }
    }
    
    /**
     * Open app settings to allow the user to grant permissions manually
     */
    fun Context.openAppSettings() {
        val intent = android.content.Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            android.net.Uri.fromParts("package", packageName, null)
        )
        startActivity(intent)
    }
    
    /**
     * Show a dialog explaining why the permissions are needed
     */
    fun Fragment.showPermissionRationaleDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit = {}
    ) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ -> onConfirm() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> onDismiss() }
            .setOnCancelListener { onDismiss() }
            .show()
    }
    
    /**
     * Show a dialog explaining that the user needs to grant permissions in settings
     */
    fun Fragment.showSettingsDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit = { openAppSettings() },
        onDismiss: () -> Unit = {}
    ) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.settings) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.cancel) { _, _ -> onDismiss() }
            .setOnCancelListener { onDismiss() }
            .show()
    }
    
    
    /**
     * Check if the app has a specific permission
     */
    fun Context.hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if the app has all the required permissions
     */
    fun Context.hasAllPermissions(permissions: Array<String>): Boolean {
        return permissions.all { hasPermission(it) }
    }
    
    /**
     * Check if the app has any of the given permissions
     */
    fun Context.hasAnyPermission(permissions: Array<String>): Boolean {
        return permissions.any { hasPermission(it) }
    }
}
