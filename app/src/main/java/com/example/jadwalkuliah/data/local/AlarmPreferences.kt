package com.example.jadwalkuliah.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.jadwalkuliah.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.alarmDataStore: DataStore<Preferences> by preferencesDataStore(name = "alarm_settings")

class AlarmPreferences(private val context: Context) {

    companion object {
        val ALARM_RINGTONE_RES_ID = intPreferencesKey("alarm_ringtone_res_id")
    }

    val alarmRingtoneResId: Flow<Int> = context.alarmDataStore.data.map { preferences ->
        preferences[ALARM_RINGTONE_RES_ID] ?: R.raw.alarm_1
    }

    suspend fun saveAlarmRingtone(resId: Int) {
        context.alarmDataStore.edit { preferences ->
            preferences[ALARM_RINGTONE_RES_ID] = resId
        }
    }
}
