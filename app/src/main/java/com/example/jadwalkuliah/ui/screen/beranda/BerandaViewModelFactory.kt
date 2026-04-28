package com.example.jadwalkuliah.ui.screen.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jadwalkuliah.data.repository.JadwalRepository
import com.example.jadwalkuliah.data.repository.TugasRepository
import com.example.jadwalkuliah.data.repository.UserProfileRepository

class BerandaViewModelFactory(
    private val jadwalRepository: JadwalRepository,
    private val tugasRepository: TugasRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BerandaViewModel::class.java)) {
            return BerandaViewModel(jadwalRepository, tugasRepository, userProfileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
