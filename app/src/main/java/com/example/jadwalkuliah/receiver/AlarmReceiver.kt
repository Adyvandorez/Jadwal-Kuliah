package com.example.jadwalkuliah.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.jadwalkuliah.MainActivity
import com.example.jadwalkuliah.R

import android.media.MediaPlayer
import com.example.jadwalkuliah.data.local.AlarmPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import android.util.Log

import com.example.jadwalkuliah.util.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val judul = intent.getStringExtra("JUDUL") ?: intent.getStringExtra("TITLE") ?: "Pengingat"
        val pesan = intent.getStringExtra("MESSAGE") ?: ""
        val id = intent.getIntExtra("ID", 0)
        val channelId = intent.getStringExtra("CHANNEL_ID")

        Log.d("AlarmReceiver", "Alarm dipicu untuk: $judul (ID: $id, Channel: $channelId)")

        if (channelId != null) {
            // Jika ada channelId, anggap ini notifikasi biasa (Jadwal/Tugas)
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showNotification(id, judul, pesan, channelId)
        } else {
            // Jika tidak ada channelId, ini adalah Alarm Utama (Looping + Full Screen)
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("JUDUL", judul)
                putExtra("ID", id)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
