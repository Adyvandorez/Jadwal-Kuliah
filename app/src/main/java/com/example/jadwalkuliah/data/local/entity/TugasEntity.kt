package com.example.jadwalkuliah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tugas")
data class TugasEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val deskripsi: String,
    val deadline: Long?,
    val kategori: String,
    val isCompleted: Boolean = false,
    val lampiran: List<String> = emptyList()
)
