package com.example.jadwalkuliah.data.local.dao

import androidx.room.*
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JadwalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJadwal(jadwal: JadwalEntity): Long

    @Update
    suspend fun updateJadwal(jadwal: JadwalEntity)

    @Delete
    suspend fun deleteJadwal(jadwal: JadwalEntity)

    @Query("""
        SELECT * FROM jadwal 
        ORDER BY 
        CASE hari
            WHEN 'Senin' THEN 1
            WHEN 'Selasa' THEN 2
            WHEN 'Rabu' THEN 3
            WHEN 'Kamis' THEN 4
            WHEN 'Jumat' THEN 5
            WHEN 'Sabtu' THEN 6
            WHEN 'Minggu' THEN 7
        END, waktuMulai
    """)
    fun getAllJadwal(): Flow<List<JadwalEntity>>

    @Query("SELECT * FROM jadwal WHERE hari = :hari ORDER BY waktuMulai")
    fun getJadwalByHari(hari: String): Flow<List<JadwalEntity>>

    @Query("SELECT * FROM jadwal WHERE id = :id")
    suspend fun getJadwalById(id: Int): JadwalEntity?
}
