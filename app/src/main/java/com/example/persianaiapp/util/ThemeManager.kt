package com.example.persianaiapp.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.persianaiapp.R
import com.example.persianaiapp.data.local.SettingsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages app theming including dark mode, font scaling, and RTL support
 */
@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager
) {
    
    companion object {
        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_BATTERY_SAVER = "battery"
        
        // Font scale factors
        const val FONT_SCALE_SMALL = 0.85f
        const val FONT_SCALE_NORMAL = 1.0f
        const val FONT_SCALE_LARGE = 1.15f
        const val FONT_SCALE_LARGER = 1.3f
        const val FONT_SCALE_LARGEST = 1.5f
    }
    
    /**
     * Apply the saved theme settings to the app
     */
    fun applyTheme() {
        // Apply theme mode
        when (settingsManager.getThemePreference()) {
            THEME_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            THEME_BATTERY_SAVER -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
        
        // Apply font scale
        applyFontScale()
    }
    
    /**
     * Apply the saved font scale to the app
     */
    fun applyFontScale() {
        val fontScale = when (settingsManager.getFontSizePreference()) {
            "small" -> FONT_SCALE_SMALL
            "large" -> FONT_SCALE_LARGE
            "larger" -> FONT_SCALE_LARGER
            "largest" -> FONT_SCALE_LARGEST
            else -> FONT_SCALE_NORMAL // Default to normal
        }
        
        val configuration = context.resources.configuration
        if (configuration.fontScale != fontScale) {
            configuration.fontScale = fontScale
            val metrics = context.resources.displayMetrics
            val windowManager = ContextCompat.getSystemService(context, android.view.WindowManager::class.java)
            windowManager?.defaultDisplay?.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, metrics)
        }
    }
    
    /**
     * Check if the current theme is dark
     */
    fun isDarkTheme(): Boolean {
        return when (settingsManager.getThemePreference()) {
            THEME_SYSTEM -> {
                val nightModeFlags = context.resources.configuration.uiMode and 
                    Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
            THEME_DARK -> true
            THEME_BATTERY_SAVER -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context.getSystemService(Activity.POWER_SERVICE)
                        ?.let { powerManager ->
                            val powerManagerClass = powerManager.javaClass
                            val isPowerSaveMode = powerManagerClass.getMethod("isPowerSaveMode").invoke(powerManager) as? Boolean
                            isPowerSaveMode == true
                        } ?: false
                } else {
                    false
                }
            }
            else -> false
        }
    }
    
    /**
     * Get the current theme resource ID
     */
    fun getThemeResId(): Int {
        return if (isDarkTheme()) {
            R.style.Theme_PersianAIAssistant_Dark
        } else {
            R.style.Theme_PersianAIAssistant_Light
        }
    }
    
    /**
     * Apply the theme to an activity
     */
    fun applyThemeToActivity(activity: Activity) {
        activity.setTheme(getThemeResId())
        
        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = activity.window
            window.statusBarColor = ContextCompat.getColor(
                activity,
                if (isDarkTheme()) R.color.md_theme_dark_surface else R.color.md_theme_light_surface
            )
            
            // Set light status bar for light theme
            var flags = window.decorView.systemUiVisibility
            flags = if (isDarkTheme()) {
                // Clear light status bar flag
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                // Set light status bar
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.decorView.systemUiVisibility = flags
        }
    }
    
    /**
     * Toggle between light and dark theme
     */
    fun toggleTheme() {
        val newTheme = if (isDarkTheme()) THEME_LIGHT else THEME_DARK
        settingsManager.setThemePreference(newTheme)
        applyTheme()
    }
    
    /**
     * Get the current font scale as a display string
     */
    fun getFontScaleDisplay(): String {
        return when (settingsManager.getFontSizePreference()) {
            "small" -> context.getString(R.string.font_size_small)
            "large" -> context.getString(R.string.font_size_large)
            "larger" -> context.getString(R.string.font_size_larger)
            "largest" -> context.getString(R.string.font_size_largest)
            else -> context.getString(R.string.font_size_normal)
        }
    }
    
    /**
     * Get the current theme display name
     */
    fun getThemeDisplay(): String {
        return when (settingsManager.getThemePreference()) {
            THEME_SYSTEM -> context.getString(R.string.theme_system)
            THEME_LIGHT -> context.getString(R.string.theme_light)
            THEME_DARK -> context.getString(R.string.theme_dark)
            THEME_BATTERY_SAVER -> context.getString(R.string.theme_battery)
            else -> context.getString(R.string.theme_system)
        }
    }
}
