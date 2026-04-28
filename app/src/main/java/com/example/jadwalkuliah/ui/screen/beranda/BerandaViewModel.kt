package com.example.jadwalkuliah.ui.screen.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import com.example.jadwalkuliah.data.repository.JadwalRepository
import com.example.jadwalkuliah.data.repository.TugasRepository
import com.example.jadwalkuliah.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*

class BerandaViewModel(
    private val jadwalRepository: JadwalRepository,
    private val tugasRepository: TugasRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val currentDay: String
        get() = SimpleDateFormat("EEEE", Locale("id", "ID")).format(Date())

    val userName: StateFlow<String> = userProfileRepository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Mahasiswa")

    val photoPath: StateFlow<String?> = userProfileRepository.photoPath
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayJadwal: StateFlow<List<JadwalEntity>> =
        jadwalRepository.getJadwalByHari(currentDay)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTugas: StateFlow<List<TugasEntity>> =
        tugasRepository.getTugasByStatus(false)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date())
    }
}
