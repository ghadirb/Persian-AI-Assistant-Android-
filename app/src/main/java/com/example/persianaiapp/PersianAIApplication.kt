package com.example.persianaiapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PersianAIApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize app components here
    }
}
