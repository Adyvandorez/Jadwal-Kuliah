package com.example.jadwalkuliah.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.jadwalkuliah.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_JADWAL_ID = "channel_jadwal_v2"
        const val CHANNEL_TUGAS_ID = "channel_tugas"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.notif_jadwal}")
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            // Channel Jadwal dengan suara khusus (play once)
            val nameJadwal = "Jadwal Kuliah"
            val descriptionJadwal = "Notifikasi untuk pengingat jadwal kuliah"
            val channelJadwal = NotificationChannel(
                CHANNEL_JADWAL_ID, 
                nameJadwal, 
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = descriptionJadwal
                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }

            // Channel Tugas (Default sound)
            val nameTugas = "Tugas & Catatan"
            val descriptionTugas = "Notifikasi untuk deadline tugas"
            val channelTugas = NotificationChannel(
                CHANNEL_TUGAS_ID, 
                nameTugas, 
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = descriptionTugas
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelJadwal)
            notificationManager.createNotificationChannel(channelTugas)
        }
    }

    fun showNotification(id: Int, title: String, message: String, channelId: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Set sound specifically for older Android versions
        if (channelId == CHANNEL_JADWAL_ID) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.notif_jadwal}")
            builder.setSound(soundUri)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }
}
