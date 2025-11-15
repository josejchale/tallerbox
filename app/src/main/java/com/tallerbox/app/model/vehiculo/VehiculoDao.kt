package com.tallerbox.app.model.vehiculo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface  VehiculoDao{
    @Insert
    suspend fun insertar(vehiculo: VehiculoEntity):Long

    @Update
    suspend fun actualizar(vehiculo: VehiculoEntity)

    @Query("SELECT * FROM vehiculos WHERE id = :vehiculoId LIMIT 1")
    suspend fun obtenerVehiculoPorId(vehiculoId: Int): VehiculoEntity?

    @Query(value = "SELECT * FROM vehiculos")
    fun obtenerTodosFlow(): Flow<List<VehiculoEntity>>

    @Query("SELECT * FROM vehiculos WHERE clienteId = :clienteId")
    fun obtenerPorClienteFlow(clienteId: Int): Flow<List<VehiculoEntity>>

    @Delete
    suspend fun eliminar(vehiculo: VehiculoEntity)

}
