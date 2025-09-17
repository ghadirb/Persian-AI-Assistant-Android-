package com.example.persianaiapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor() {

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.FOREGROUND_SERVICE
        )

        val OPTIONAL_PERMISSIONS = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.USE_BIOMETRIC,
            Manifest.permission.USE_FINGERPRINT
        )
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAllRequiredPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all { hasPermission(context, it) }
    }

    fun getMissingRequiredPermissions(context: Context): List<String> {
        return REQUIRED_PERMISSIONS.filter { !hasPermission(context, it) }
    }

    fun getMissingOptionalPermissions(context: Context): List<String> {
        return OPTIONAL_PERMISSIONS.filter { !hasPermission(context, it) }
    }

    fun createPermissionLauncher(
        fragment: Fragment,
        onPermissionsGranted: (Map<String, Boolean>) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            onPermissionsGranted(permissions)
        }
    }

    fun requestRequiredPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(REQUIRED_PERMISSIONS)
    }

    fun requestOptionalPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(OPTIONAL_PERMISSIONS)
    }

    fun requestSpecificPermissions(
        launcher: ActivityResultLauncher<Array<String>>,
        permissions: Array<String>
    ) {
        launcher.launch(permissions)
    }

    fun getPermissionExplanation(permission: String): String {
        return when (permission) {
            Manifest.permission.RECORD_AUDIO -> "برای ضبط پیام‌های صوتی و تشخیص گفتار"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "برای خواندن فایل‌های مدل و پشتیبان"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "برای ذخیره فایل‌های پشتیبان و مدل‌ها"
            Manifest.permission.INTERNET -> "برای اتصال به سرویس‌های هوش مصنوعی آنلاین"
            Manifest.permission.ACCESS_NETWORK_STATE -> "برای بررسی وضعیت اتصال اینترنت"
            Manifest.permission.CALL_PHONE -> "برای تماس گرفتن از طریق دستیار"
            Manifest.permission.SEND_SMS -> "برای ارسال پیامک"
            Manifest.permission.READ_CONTACTS -> "برای دسترسی به مخاطبین"
            Manifest.permission.WRITE_CONTACTS -> "برای اضافه کردن مخاطبین جدید"
            Manifest.permission.READ_CALENDAR -> "برای مشاهده رویدادهای تقویم"
            Manifest.permission.WRITE_CALENDAR -> "برای اضافه کردن رویداد به تقویم"
            Manifest.permission.ACCESS_FINE_LOCATION -> "برای سرویس‌های مکان‌یابی دقیق"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "برای سرویس‌های مکان‌یابی تقریبی"
            Manifest.permission.USE_BIOMETRIC -> "برای احراز هویت با اثر انگشت یا تشخیص چهره"
            Manifest.permission.USE_FINGERPRINT -> "برای احراز هویت با اثر انگشت"
            else -> "برای عملکرد صحیح برنامه"
        }
    }

    fun requestVoiceInputPermission(context: Context) {
        val voicePermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_MICROPHONE
        )
        
        val missingPermissions = voicePermissions.filter { !hasPermission(context, it) }
        
        if (missingPermissions.isNotEmpty()) {
            // Note: This method should be called with an ActivityResultLauncher
            // For now, we'll just log the missing permissions
            android.util.Log.w("PermissionManager", "Missing voice permissions: $missingPermissions")
        }
    }
}
