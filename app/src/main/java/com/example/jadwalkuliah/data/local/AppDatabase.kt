package com.example.jadwalkuliah.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jadwalkuliah.data.local.dao.JadwalDao
import com.example.jadwalkuliah.data.local.dao.PengingatDao
import com.example.jadwalkuliah.data.local.dao.TugasDao
import com.example.jadwalkuliah.data.local.entity.JadwalEntity
import com.example.jadwalkuliah.data.local.entity.PengingatEntity
import com.example.jadwalkuliah.data.local.entity.TugasEntity

@Database(entities = [JadwalEntity::class, TugasEntity::class, PengingatEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun jadwalDao(): JadwalDao
    abstract fun tugasDao(): TugasDao
    abstract fun pengingatDao(): PengingatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jadwal_kuliah_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
