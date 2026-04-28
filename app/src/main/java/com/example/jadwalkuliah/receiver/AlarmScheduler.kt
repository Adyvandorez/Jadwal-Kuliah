package com.example.jadwalkuliah.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import java.util.*

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(pengingat: PengingatEntity) {
        if (!pengingat.isActive) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Jangan panggil setExact jika tidak ada izin untuk menghindari crash
                // Untuk "Daily" kita bisa pakai setRepeating yang biasanya tidak butuh izin exact,
                // tapi untuk konsistensi dan keamanan kita cek di sini.
                if (pengingat.tipeUlang != "Daily") return
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("JUDUL", pengingat.judul)
            putExtra("ID", pengingat.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            pengingat.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val calendar = Calendar.getInstance().apply {
                val timeParts = pengingat.waktu.split(":").map { it.trim() }
                if (timeParts.size == 2) {
                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    set(Calendar.MINUTE, timeParts[1].toInt())
                    set(Calendar.SECOND, 0)
                }
                
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            when (pengingat.tipeUlang) {
                "Daily" -> {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                }
                "Sekali" -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
                "Custom" -> {
                    scheduleNextCustom(pengingat, pendingIntent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleNextCustom(pengingat: PengingatEntity, pendingIntent: PendingIntent) {
        val daysOfWeekMap = mapOf(
            "Minggu" to Calendar.SUNDAY,
            "Senin" to Calendar.MONDAY,
            "Selasa" to Calendar.TUESDAY,
            "Rabu" to Calendar.WEDNESDAY,
            "Kamis" to Calendar.THURSDAY,
            "Jumat" to Calendar.FRIDAY,
            "Sabtu" to Calendar.SATURDAY
        )

        val selectedDays = pengingat.hariTerpilih.mapNotNull { daysOfWeekMap[it] }.sorted()
        if (selectedDays.isEmpty()) return

        try {
            val now = Calendar.getInstance()
            val timeParts = pengingat.waktu.split(":").map { it.trim() }
            val alarmTime = Calendar.getInstance().apply {
                if (timeParts.size == 2) {
                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    set(Calendar.MINUTE, timeParts[1].toInt())
                    set(Calendar.SECOND, 0)
                }
            }

            var nextAlarm: Calendar? = null
            
            for (day in selectedDays) {
                val candidate = (alarmTime.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_WEEK, day)
                }
                if (candidate.timeInMillis > now.timeInMillis) {
                    nextAlarm = candidate
                    break
                }
            }

            if (nextAlarm == null) {
                nextAlarm = (alarmTime.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_WEEK, selectedDays[0])
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarm.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancel(pengingat: PengingatEntity) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            pengingat.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
