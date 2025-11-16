package com.tallerbox.app.repository.orden

import com.tallerbox.app.model.orden.OrdenServicioDao
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.model.orden.OrdenServicioEntity
import kotlinx.coroutines.flow.Flow

class OrdenRepository(private val ordenDao: OrdenServicioDao) {

    // CONSULTAS GENERALES

    fun obtenerTodasFlow(): Flow<List<OrdenServicioEntity>> =
        ordenDao.obtenerTodosFlow()

    suspend fun obtenerPorId(ordenId: Int): OrdenServicioEntity? =
        ordenDao.obtenerPorId(ordenId)

    // CONSULTAS POR FILTROS

    fun obtenerPorClienteFlow(clienteId: Int): Flow<List<OrdenServicioEntity>> =
        ordenDao.obtenerPorClienteFlow(clienteId)

    fun obtenerPorVehiculoFlow(vehiculoId: Int): Flow<List<OrdenServicioEntity>> =
        ordenDao.obtenerPorVehiculoFlow(vehiculoId)

    // CONSULTAS CON RELACIONES

    suspend fun obtenerOrdenConRelaciones(ordenId: Int): OrdenConClienteYVehiculo? =
        ordenDao.obtenerOrdenConRelaciones(ordenId)

    // INSERTAR / ACTUALIZAR / ELIMINAR

    suspend fun insertar(orden: OrdenServicioEntity): Long =
        ordenDao.insertar(orden)

    suspend fun actualizar(orden: OrdenServicioEntity) =
        ordenDao.actualizar(orden)

    suspend fun eliminar(orden: OrdenServicioEntity) =
        ordenDao.eliminar(orden)
}