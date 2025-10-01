package com.tallerbox.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tallerbox.app.model.cliente.ClienteDao
import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.model.vehiculo.VehiculoDao
import com.tallerbox.app.model.vehiculo.VehiculoEntity

@Database(entities = [
    ClienteEntity::class,
    VehiculoEntity::class
                     ], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun vehiculoDao(): VehiculoDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tallerbox_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}