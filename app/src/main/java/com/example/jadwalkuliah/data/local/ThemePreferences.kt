package com.example.jadwalkuliah.data.local

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "theme_prefs")

class ThemePreferences(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme_mode")

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[themeKey] ?: "Dark"
        }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = mode
        }
    }
}
