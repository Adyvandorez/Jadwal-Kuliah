package com.example.jadwalkuliah.ui.screen.pengingat

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jadwalkuliah.data.repository.PengingatRepository

class PengingatViewModelFactory(
    private val application: Application,
    private val repository: PengingatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PengingatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PengingatViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
