package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jadwalkuliah.data.repository.JadwalRepository

class JadwalViewModelFactory(
    private val repository: JadwalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JadwalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JadwalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
