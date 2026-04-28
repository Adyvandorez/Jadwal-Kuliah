package com.example.jadwalkuliah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jadwal")
data class JadwalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaMatkul: String,
    val dosen: String,
    val hari: String,
    val waktuMulai: String,
    val waktuSelesai: String,
    val ruangan: String
)
