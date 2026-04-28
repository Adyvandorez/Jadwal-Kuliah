package com.example.jadwalkuliah.data.local.dao

import androidx.room.*
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PengingatDao {
    @Query("SELECT * FROM pengingat ORDER BY waktu ASC")
    fun getAllPengingat(): Flow<List<PengingatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPengingat(pengingat: PengingatEntity): Long

    @Update
    suspend fun updatePengingat(pengingat: PengingatEntity)

    @Delete
    suspend fun deletePengingat(pengingat: PengingatEntity)

    @Query("SELECT * FROM pengingat WHERE id = :id")
    suspend fun getPengingatById(id: Int): PengingatEntity?
}
