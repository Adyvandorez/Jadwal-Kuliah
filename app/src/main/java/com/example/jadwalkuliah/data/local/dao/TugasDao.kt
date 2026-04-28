package com.example.jadwalkuliah.data.local.dao

import androidx.room.*
import com.example.jadwalkuliah.data.local.entity.TugasEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTugas(tugas: TugasEntity)

    @Update
    suspend fun updateTugas(tugas: TugasEntity)

    @Delete
    suspend fun deleteTugas(tugas: TugasEntity)

    @Query("SELECT * FROM tugas ORDER BY deadline ASC")
    fun getAllTugas(): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE isCompleted = :isCompleted ORDER BY deadline ASC")
    fun getTugasByStatus(isCompleted: Boolean): Flow<List<TugasEntity>>

    @Query("SELECT * FROM tugas WHERE id = :id")
    suspend fun getTugasById(id: Int): TugasEntity?
}
