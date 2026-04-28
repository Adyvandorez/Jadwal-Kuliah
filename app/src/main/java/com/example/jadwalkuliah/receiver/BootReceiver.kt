package com.example.jadwalkuliah.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.jadwalkuliah.data.local.AppDatabase
import com.example.jadwalkuliah.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = AppDatabase.getDatabase(context)
            val alarmScheduler = AlarmScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                val allJadwal = database.jadwalDao().getAllJadwal().first()
                allJadwal.forEach { alarmScheduler.scheduleJadwalAlarm(it) }

                val allTugas = database.tugasDao().getTugasByStatus(false).first()
                allTugas.forEach { alarmScheduler.scheduleTugasAlarm(it) }
            }
        }
    }
}
