package com.tallerbox.app.repository

import com.tallerbox.app.model.orden.OrdenServicioDao
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.model.orden.OrdenServicioEntity
import kotlinx.coroutines.flow.Flow

class OrdenRepository(private val ordenDao: OrdenServicioDao) {

    fun obtenerTodasFlow(): Flow<List<OrdenServicioEntity>> = ordenDao.obtenerTodosFlow()

    fun obtenerPorClienteFlow(clienteId: Int): Flow<List<OrdenServicioEntity>> =
        ordenDao.obtenerPorClienteFlow(clienteId)

    fun obtenerPorVehiculoFlow(vehiculoId: Int): Flow<List<OrdenServicioEntity>> {
        return ordenDao.obtenerPorVehiculoFlow(vehiculoId)
    }

    suspend fun obtenerOrdenConRelaciones(ordenId: Int): OrdenConClienteYVehiculo? =
        ordenDao.obtenerOrdenConRelaciones(ordenId)

    suspend fun insertar(orden: OrdenServicioEntity): Long = ordenDao.insertar(orden)

    suspend fun actualizar(orden: OrdenServicioEntity) = ordenDao.actualizar(orden)

    suspend fun eliminar(orden: OrdenServicioEntity) = ordenDao.eliminar(orden)
}
