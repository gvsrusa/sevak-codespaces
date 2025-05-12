package com.agriconnect

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AgriConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here if needed
        // For example, Timber for logging, etc.
    }
}