package com.example.jadwalkuliah

import android.app.Application
import com.example.jadwalkuliah.util.NotificationHelper

class JadwalKuliahApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Notification Channels
        NotificationHelper(this)
    }
}
