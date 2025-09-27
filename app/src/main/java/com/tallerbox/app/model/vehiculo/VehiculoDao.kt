package com.tallerbox.app.model.vehiculo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface  VehiculoDao{
    @Insert
    suspend fun insertar(vehiculo: VehiculoEntity)

    @Query(value = "SELECT * FROM vehiculos")
    fun obtenerTodosFlow(): Flow<List<VehiculoEntity>>
}
