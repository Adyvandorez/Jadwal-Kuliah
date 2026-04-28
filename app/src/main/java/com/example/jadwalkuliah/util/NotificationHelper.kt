package com.example.jadwalkuliah.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.jadwalkuliah.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_JADWAL_ID = "channel_jadwal"
        const val CHANNEL_TUGAS_ID = "channel_tugas"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nameJadwal = "Jadwal Kuliah"
            val descriptionJadwal = "Notifikasi untuk jadwal kuliah hari ini"
            val importanceJadwal = NotificationManager.IMPORTANCE_HIGH
            val channelJadwal = NotificationChannel(CHANNEL_JADWAL_ID, nameJadwal, importanceJadwal).apply {
                description = descriptionJadwal
            }

            val nameTugas = "Tugas & Catatan"
            val descriptionTugas = "Notifikasi untuk deadline tugas"
            val importanceTugas = NotificationManager.IMPORTANCE_HIGH
            val channelTugas = NotificationChannel(CHANNEL_TUGAS_ID, nameTugas, importanceTugas).apply {
                description = descriptionTugas
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelJadwal)
            notificationManager.createNotificationChannel(channelTugas)
        }
    }

    fun showNotification(id: Int, title: String, message: String, channelId: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use default for now
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }
}
