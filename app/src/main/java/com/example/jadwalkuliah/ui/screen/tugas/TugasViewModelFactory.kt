package com.example.jadwalkuliah.ui.screen.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jadwalkuliah.data.repository.TugasRepository

class TugasViewModelFactory(
    private val repository: TugasRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TugasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TugasViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
