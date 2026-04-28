package com.example.jadwalkuliah.ui.screen.jadwal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.data.repository.JadwalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JadwalViewModel(private val repository: JadwalRepository) : ViewModel() {

    val allJadwal: StateFlow<List<JadwalEntity>> = repository.getAllJadwal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertJadwal(jadwal: JadwalEntity, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertJadwal(jadwal)
            onResult(id)
        }
    }

    fun updateJadwal(jadwal: JadwalEntity, onResult: () -> Unit) {
        viewModelScope.launch {
            repository.updateJadwal(jadwal)
            onResult()
        }
    }

    fun deleteJadwal(jadwal: JadwalEntity) {
        viewModelScope.launch {
            repository.deleteJadwal(jadwal)
        }
    }

    suspend fun getJadwalById(id: Int): JadwalEntity? {
        return repository.getJadwalById(id)
    }
}
