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

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val judul = intent.getStringExtra("JUDUL") ?: "Pengingat"
        val id = intent.getIntExtra("ID", 0)

        showNotification(context, judul, id)
    }

    private fun showNotification(context: Context, judul: String, id: Int) {
        val channelId = "pengingat_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pengingat Rutin",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk pengingat rutin harian"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, id, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti dengan icon yang sesuai jika ada
            .setContentTitle("⏰ Waktunya: $judul!")
            .setContentText("Klik untuk melihat detail pengingat.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
