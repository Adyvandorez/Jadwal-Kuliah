package com.example.jadwalkuliah.ui.screen.pengingat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import com.example.jadwalkuliah.data.repository.PengingatRepository
import com.example.jadwalkuliah.receiver.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PengingatViewModel(
    application: Application,
    private val repository: PengingatRepository
) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler(application)

    val allPengingat: StateFlow<List<PengingatEntity>> = repository.allPengingat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(pengingat: PengingatEntity) {
        viewModelScope.launch {
            val id = repository.insert(pengingat)
            val insertedPengingat = pengingat.copy(id = id.toInt())
            if (insertedPengingat.isActive) {
                alarmScheduler.schedule(insertedPengingat)
            }
        }
    }

    fun update(pengingat: PengingatEntity) {
        viewModelScope.launch {
            repository.update(pengingat)
            if (pengingat.isActive) {
                alarmScheduler.schedule(pengingat)
            } else {
                alarmScheduler.cancel(pengingat)
            }
        }
    }

    fun delete(pengingat: PengingatEntity) {
        viewModelScope.launch {
            alarmScheduler.cancel(pengingat)
            repository.delete(pengingat)
        }
    }

    fun togglePengingat(pengingat: PengingatEntity) {
        val updated = pengingat.copy(isActive = !pengingat.isActive)
        update(updated)
    }

    suspend fun getPengingatById(id: Int): PengingatEntity? {
        return repository.getPengingatById(id)
    }
}
