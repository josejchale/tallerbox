package com.tallerbox.app.model.orden

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OrdenServicioDao {

    // INSERTAR / ACTUALIZAR / ELIMINAR
    @Insert
    suspend fun insertar(orden: OrdenServicioEntity): Long

    @Update
    suspend fun actualizar(orden: OrdenServicioEntity)

    @Delete
    suspend fun eliminar(orden: OrdenServicioEntity)

    // CONSULTAS
    @Query("SELECT * FROM orden_servicio ORDER BY fechaIngreso DESC")
    fun obtenerTodosFlow(): Flow<List<OrdenServicioEntity>>

    @Query("SELECT * FROM orden_servicio WHERE id = :ordenId LIMIT 1")
    suspend fun obtenerPorId(ordenId: Int?): OrdenServicioEntity?

    // CONSULTAS POR FILTROS

    @Query("SELECT * FROM orden_servicio WHERE clienteId = :clienteId ORDER BY fechaIngreso DESC")
    fun obtenerPorClienteFlow(clienteId: Int): Flow<List<OrdenServicioEntity>>

    @Query("SELECT * FROM orden_servicio WHERE vehiculoId = :vehiculoId ORDER BY fechaIngreso DESC")
    fun obtenerPorVehiculoFlow(vehiculoId: Int): Flow<List<OrdenServicioEntity>>

    // CONSULTAS CON RELACIONES
    @Transaction
    @Query("SELECT * FROM orden_servicio WHERE id = :ordenId")
    suspend fun obtenerOrdenConRelaciones(ordenId: Int): OrdenConClienteYVehiculo?

    // ACTUALIZACIONES ESPECÍFICAS

    @Query("""
        UPDATE orden_servicio
    SET estadoOrden = :estado,
        fechaEntregaReal = :fechaEntregaReal,
        entrega = :entrega
    WHERE id = :ordenId
    """)
    suspend fun actualizarEstadoYEntrega(
        ordenId: Int,
        estado: String,
        fechaEntregaReal: Date?,
        entrega: String
    )

    //CONSULTAR POR ESTADO DE LA ORDEN
    @Query("SELECT COUNT(*) FROM orden_servicio WHERE estadoOrden = :estado")
    fun contarPorEstadoFlow(estado: String): Flow<Int>
}
