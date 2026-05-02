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
                return
            }
        }

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
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()

                // 20 Menit Sebelum
                scheduleOffsetAlarm(jadwal, dayOfWeek, hour, minute, 20, 3000)
                
                // 5 Menit Sebelum
                scheduleOffsetAlarm(jadwal, dayOfWeek, hour, minute, 5, 4000)
                
                // Alarm Utama (Tepat Waktu) - Tetap dipertahankan jika diperlukan, 
                // namun instruksi fokus pada 20m & 5m. 
                // Kita biarkan yang ID asli tetap ada agar tidak merusak behavior lama jika user masih menginginkannya.
                val mainCalendar = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, dayOfWeek)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.WEEK_OF_YEAR, 1)
                    }
                }
                
                scheduleAlarm(
                    id = jadwal.id,
                    time = mainCalendar.timeInMillis,
                    title = "Pengingat Jadwal",
                    message = "${jadwal.namaMatkul} (${jadwal.waktuMulai})",
                    channelId = NotificationHelper.CHANNEL_JADWAL_ID
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleOffsetAlarm(jadwal: JadwalEntity, dayOfWeek: Int, hour: Int, minute: Int, offsetMinutes: Int, idOffset: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -offsetMinutes)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        scheduleAlarm(
            id = jadwal.id + idOffset,
            time = calendar.timeInMillis,
            title = "Pengingat Jadwal",
            message = "${jadwal.namaMatkul}: Kurang $offsetMinutes menit lagi",
            channelId = NotificationHelper.CHANNEL_JADWAL_ID
        )
    }

    fun cancelJadwalAlarms(jadwalId: Int) {
        cancelAlarm(jadwalId)
        cancelAlarm(jadwalId + 3000)
        cancelAlarm(jadwalId + 4000)
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
