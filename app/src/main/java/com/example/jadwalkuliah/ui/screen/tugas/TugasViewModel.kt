package com.example.jadwalkuliah.ui.screen.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.data.repository.TugasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TugasViewModel(private val repository: TugasRepository) : ViewModel() {

    val allTugas: StateFlow<List<TugasEntity>> = repository.getAllTugas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTugas(tugas: TugasEntity) {
        viewModelScope.launch {
            repository.insertTugas(tugas)
        }
    }

    fun updateTugas(tugas: TugasEntity) {
        viewModelScope.launch {
            repository.updateTugas(tugas)
        }
    }

    fun deleteTugas(tugas: TugasEntity) {
        viewModelScope.launch {
            repository.deleteTugas(tugas)
        }
    }

    fun toggleTugasCompletion(tugas: TugasEntity) {
        viewModelScope.launch {
            repository.updateTugas(tugas.copy(isCompleted = !tugas.isCompleted))
        }
    }

    suspend fun getTugasById(id: Int): TugasEntity? {
        return repository.getTugasById(id)
    }
}
