package com.example.jadwalkuliah.data.repository

import com.example.jadwalkuliah.data.local.dao.JadwalDao
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import kotlinx.coroutines.flow.Flow

class JadwalRepository(private val jadwalDao: JadwalDao) {

    fun getAllJadwal(): Flow<List<JadwalEntity>> = jadwalDao.getAllJadwal()

    fun getJadwalByHari(hari: String): Flow<List<JadwalEntity>> = jadwalDao.getJadwalByHari(hari)

    suspend fun getJadwalById(id: Int): JadwalEntity? = jadwalDao.getJadwalById(id)

    suspend fun insertJadwal(jadwal: JadwalEntity): Long {
        return jadwalDao.insertJadwal(jadwal)
    }

    suspend fun updateJadwal(jadwal: JadwalEntity) {
        jadwalDao.updateJadwal(jadwal)
    }

    suspend fun deleteJadwal(jadwal: JadwalEntity) {
        jadwalDao.deleteJadwal(jadwal)
    }
}
