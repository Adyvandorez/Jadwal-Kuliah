package com.example.jadwalkuliah.data.repository

import com.example.jadwalkuliah.data.local.dao.TugasDao
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import kotlinx.coroutines.flow.Flow

class TugasRepository(private val tugasDao: TugasDao) {

    fun getAllTugas(): Flow<List<TugasEntity>> = tugasDao.getAllTugas()

    fun getTugasByStatus(isCompleted: Boolean): Flow<List<TugasEntity>> =
        tugasDao.getTugasByStatus(isCompleted)

    suspend fun getTugasById(id: Int): TugasEntity? = tugasDao.getTugasById(id)

    suspend fun insertTugas(tugas: TugasEntity) {
        tugasDao.insertTugas(tugas)
    }

    suspend fun updateTugas(tugas: TugasEntity) {
        tugasDao.updateTugas(tugas)
    }

    suspend fun deleteTugas(tugas: TugasEntity) {
        tugasDao.deleteTugas(tugas)
    }
}
