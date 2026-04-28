package com.example.jadwalkuliah.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")

class UserProfilePreferences(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val PHOTO_PATH = stringPreferencesKey("photo_path")
    }

    val userName: Flow<String> = context.profileDataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "Mahasiswa"
    }

    val photoPath: Flow<String?> = context.profileDataStore.data.map { preferences ->
        preferences[PHOTO_PATH]
    }

    suspend fun saveUserProfile(name: String, path: String?) {
        context.profileDataStore.edit { preferences ->
            preferences[USER_NAME] = name
            if (path != null) {
                preferences[PHOTO_PATH] = path
            } else {
                preferences.remove(PHOTO_PATH)
            }
        }
    }
}
