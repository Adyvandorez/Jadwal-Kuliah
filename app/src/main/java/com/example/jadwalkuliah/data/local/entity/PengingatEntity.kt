package com.example.jadwalkuliah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pengingat")
data class PengingatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val waktu: String, // format HH:mm
    val tipeUlang: String, // "Daily", "Sekali", "Custom"
    val hariTerpilih: List<String>, // Contoh: ["Senin", "Kamis"]
    val isActive: Boolean = true
)
