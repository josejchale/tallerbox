package com.tallerbox.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.model.cliente.ClienteDao
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import com.tallerbox.app.model.vehiculo.VehiculoDao
import com.tallerbox.app.model.orden.OrdenServicioEntity
import com.tallerbox.app.model.orden.OrdenServicioDao
import com.tallerbox.app.model.orden.OrdenTypeConverters

@Database(
    entities = [ClienteEntity::class, VehiculoEntity::class, OrdenServicioEntity::class],
    version = 3, // incrementa versión al añadir entidad
    exportSchema = false
)
@TypeConverters(OrdenTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun vehiculoDao(): VehiculoDao
    abstract fun ordenServicioDao(): OrdenServicioDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tallerbox_db"
                )
                    // Durante desarrollo evita errores de migración; en producción escribe migraciones
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
