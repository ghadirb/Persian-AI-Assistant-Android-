package com.example.persianaiapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.persianaiapp.ui.chat.ChatScreen
import com.example.persianaiapp.ui.enhanced.EnhancedChatScreen
import com.example.persianaiapp.ui.enhanced.EnhancedSettingsScreen
import com.example.persianaiapp.ui.enhanced.EnhancedWelcomeScreen
import com.example.persianaiapp.ui.navigation.AppNavigation
import com.example.persianaiapp.ui.offline.OfflineModeScreen
import com.example.persianaiapp.ui.online.OnlineModeScreen
import com.example.persianaiapp.ui.settings.SettingsScreen
import com.example.persianaiapp.ui.startup.KeySetupScreen
import com.example.persianaiapp.ui.theme.PersianAIAssistantTheme
import com.example.persianaiapp.ui.welcome.WelcomeScreen
import com.example.persianaiapp.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.filterValues { !it }.keys
        if (deniedPermissions.isNotEmpty()) {
            // Handle denied permissions
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            // Initialize Hilt before super.onCreate
            super.onCreate(savedInstanceState)
            
            // Install splash screen with error handling
            try {
                installSplashScreen()
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error installing splash screen", e)
            }
            
            // Request voice input permission if needed
            try {
                permissionManager.requestVoiceInputPermission(this)
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error requesting permissions", e)
            }
             
            setContent {
                PersianAIAssistantTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Fatal error during onCreate", e)
            e.printStackTrace()
            
            // Try to show a toast or error message
            try {
                android.widget.Toast.makeText(this, "خطا در راه‌اندازی برنامه", android.widget.Toast.LENGTH_LONG).show()
            } catch (toastException: Exception) {
                android.util.Log.e("MainActivity", "Error showing toast", toastException)
            }
            
            // Don't finish() immediately, give time for error to be logged
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                finish()
            }, 2000)
        }
        
        // Handle app shortcuts
        handleShortcutIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleShortcutIntent(it) }
    }
    
    private fun handleShortcutIntent(intent: Intent) {
        when (intent.getStringExtra("shortcut_action")) {
            "online_chat" -> {
                // Navigate directly to online chat
                // This will be handled by the navigation system
            }
            "offline_chat" -> {
                // Navigate directly to offline chat
                // This will be handled by the navigation system
            }
            "voice_assistant" -> {
                // Start voice assistant mode
                // This will be handled by the navigation system
            }
        }
    }
    
    private fun checkPermissions() {
        try {
            val requiredPermissions = arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.WAKE_LOCK,
                android.Manifest.permission.FOREGROUND_SERVICE
            )
            
            val missingPermissions = requiredPermissions.filter { 
                androidx.core.content.ContextCompat.checkSelfPermission(this, it) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED 
            }
            
            if (missingPermissions.isNotEmpty()) {
                permissionLauncher.launch(missingPermissions.toTypedArray())
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error checking permissions", e)
        }
    }
}