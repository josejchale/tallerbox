package com.tallerbox.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tallerbox.app.model.cliente.ClienteDao
import com.tallerbox.app.model.cliente.ClienteEntity

@Database(entities = [ClienteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tallerbox_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}