package com.example.jadwalkuliah.data.repository

import com.example.jadwalkuliah.data.local.UserProfilePreferences
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val userProfilePreferences: UserProfilePreferences) {

    val userName: Flow<String> = userProfilePreferences.userName
    val photoPath: Flow<String?> = userProfilePreferences.photoPath

    suspend fun saveProfile(name: String, path: String?) {
        userProfilePreferences.saveUserProfile(name, path)
    }
}
