package com.example.jadwalkuliah.data.repository

import com.example.jadwalkuliah.data.local.dao.PengingatDao
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import kotlinx.coroutines.flow.Flow

class PengingatRepository(private val pengingatDao: PengingatDao) {
    val allPengingat: Flow<List<PengingatEntity>> = pengingatDao.getAllPengingat()

    suspend fun insert(pengingat: PengingatEntity): Long {
        return pengingatDao.insertPengingat(pengingat)
    }

    suspend fun update(pengingat: PengingatEntity) {
        pengingatDao.updatePengingat(pengingat)
    }

    suspend fun delete(pengingat: PengingatEntity) {
        pengingatDao.deletePengingat(pengingat)
    }

    suspend fun getPengingatById(id: Int): PengingatEntity? {
        return pengingatDao.getPengingatById(id)
    }
}
