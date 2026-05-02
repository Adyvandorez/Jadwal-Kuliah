package com.example.jadwalkuliah.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.jadwalkuliah.R
import com.example.jadwalkuliah.data.local.AlarmPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.jadwalkuliah.MainActivity

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_SNOOZE = "ACTION_SNOOZE"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val judul = intent?.getStringExtra("JUDUL") ?: "Alarm"
        val id = intent?.getIntExtra("ID", 0) ?: 0

        when (action) {
            ACTION_STOP -> {
                Log.d("AlarmService", "Action STOP diterima")
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_SNOOZE -> {
                Log.d("AlarmService", "Action SNOOZE diterima")
                // Logika snooze sederhana: stop sekarang, nanti dijadwalkan lagi (bisa dikembangkan)
                stopSelf()
                return START_NOT_STICKY
            }
        }

        Log.d("AlarmService", "AlarmService dimulai untuk: $judul")
        startForeground(id + 1000, createCustomNotification(judul, id))

        // Launch Full Screen Activity
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra("JUDUL", judul)
            putExtra("ID", id)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(fullScreenIntent)

        serviceScope.launch {
            val alarmPreferences = AlarmPreferences(applicationContext)
            val resId = alarmPreferences.alarmRingtoneResId.first()
            
            mediaPlayer?.stop()
            mediaPlayer?.release()
            
            try {
                mediaPlayer = MediaPlayer.create(applicationContext, resId)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
                Log.d("AlarmService", "Audio alarm looping dimulai")
            } catch (e: Exception) {
                Log.e("AlarmService", "Gagal memutar audio", e)
            }
        }

        return START_STICKY
    }

    private fun createCustomNotification(judul: String, id: Int): Notification {
        val channelId = "alarm_real_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Berbunyi",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null) // Audio dihandle MediaPlayer
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, AlarmService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(
            this, id, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(this, AlarmService::class.java).apply { action = ACTION_SNOOZE }
        val snoozePendingIntent = PendingIntent.getService(
            this, id + 1, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra("JUDUL", judul)
            putExtra("ID", id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, id + 2, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val remoteViews = RemoteViews(packageName, R.layout.layout_alarm_notification).apply {
            setTextViewText(R.id.tv_time, currentTime)
            setTextViewText(R.id.tv_title, judul)
            setOnClickPendingIntent(R.id.btn_stop, stopPendingIntent)
            setOnClickPendingIntent(R.id.btn_snooze, snoozePendingIntent)
        }

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setCustomHeadsUpContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_MAX) // Diubah ke MAX untuk heads-up lebih agresif
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true) // Menggunakan PendingIntent nyata
            .setOngoing(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("AlarmService", "Service dihancurkan, audio berhenti")
    }
}
