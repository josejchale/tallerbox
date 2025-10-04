package com.tallerbox.app.model.orden

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OrdenServicioDao {

    @Insert
    suspend fun insertar(orden: OrdenServicioEntity): Long

    @Update
    suspend fun actualizar(orden: OrdenServicioEntity)

    @Delete
    suspend fun eliminar(orden: OrdenServicioEntity)

    @Query("SELECT * FROM orden_servicio ORDER BY fechaIngreso DESC")
    fun obtenerTodosFlow(): Flow<List<OrdenServicioEntity>>

    @Query("SELECT * FROM orden_servicio WHERE clienteId = :clienteId ORDER BY fechaIngreso DESC")
    fun obtenerPorClienteFlow(clienteId: Int): Flow<List<OrdenServicioEntity>>

    // NUEVO: obtener por vehiculoId
    @Query("SELECT * FROM orden_servicio WHERE vehiculoId = :vehiculoId ORDER BY fechaIngreso DESC")
    fun obtenerPorVehiculoFlow(vehiculoId: Int): Flow<List<OrdenServicioEntity>>

    @Transaction
    @Query("SELECT * FROM orden_servicio WHERE id = :ordenId")
    suspend fun obtenerOrdenConRelaciones(ordenId: Int): OrdenConClienteYVehiculo?

    @Query("UPDATE orden_servicio SET estadoOrden = :estado, fechaEntregaReal = :fechaEntregaReal WHERE id = :ordenId")
    suspend fun actualizarEstadoYEntrega(ordenId: Int, estado: String, fechaEntregaReal: Date?)

}
