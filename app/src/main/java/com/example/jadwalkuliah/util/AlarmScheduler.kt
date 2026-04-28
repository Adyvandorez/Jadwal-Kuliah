package com.example.jadwalkuliah.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.receiver.AlarmReceiver
import java.util.*

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleJadwalAlarm(jadwal: JadwalEntity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Berhenti di sini jika tidak punya izin, agar tidak crash
                return
            }
        }

        val calendar = Calendar.getInstance()
        val dayOfWeek = when (jadwal.hari) {
            "Senin" -> Calendar.MONDAY
            "Selasa" -> Calendar.TUESDAY
            "Rabu" -> Calendar.WEDNESDAY
            "Kamis" -> Calendar.THURSDAY
            "Jumat" -> Calendar.FRIDAY
            "Sabtu" -> Calendar.SATURDAY
            "Minggu" -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }

        try {
            val timeParts = jadwal.waktuMulai.split(":").map { it.trim() }
            if (timeParts.size == 2) {
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                calendar.set(Calendar.MINUTE, timeParts[1].toInt())
                calendar.set(Calendar.SECOND, 0)

                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                }

                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("ID", jadwal.id)
                    putExtra("JUDUL", "Kuliah Mulai: ${jadwal.namaMatkul}")
                    putExtra("MESSAGE", "Ruangan: ${jadwal.ruangan}")
                    putExtra("CHANNEL_ID", NotificationHelper.CHANNEL_JADWAL_ID)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    jadwal.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleTugasAlarm(tugas: TugasEntity) {
        val deadline = tugas.deadline ?: return
        
        if (tugas.isCompleted) {
            cancelTugasAlarms(tugas.id)
            return
        }

        val now = System.currentTimeMillis()
        
        // Notification at Deadline (H-0)
        if (deadline > now) {
            scheduleAlarm(
                id = tugas.id + 1000,
                time = deadline,
                title = "🔥 Deadline hari ini!",
                message = "Tugas: ${tugas.judul}",
                channelId = NotificationHelper.CHANNEL_TUGAS_ID
            )
        }

        // Notification H-1 (24 hours before)
        val hMinus1 = deadline - (24 * 60 * 60 * 1000)
        if (hMinus1 > now) {
            scheduleAlarm(
                id = tugas.id + 2000,
                time = hMinus1,
                title = "⏰ Tugas hampir deadline!",
                message = "Besok deadline: ${tugas.judul}",
                channelId = NotificationHelper.CHANNEL_TUGAS_ID
            )
        }
    }

    private fun scheduleAlarm(id: Int, time: Long, title: String, message: String, channelId: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ID", id)
            putExtra("TITLE", title)
            putExtra("MESSAGE", message)
            putExtra("CHANNEL_ID", channelId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    fun cancelTugasAlarms(tugasId: Int) {
        cancelAlarm(tugasId + 1000)
        cancelAlarm(tugasId + 2000)
    }

    fun cancelAlarm(id: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
