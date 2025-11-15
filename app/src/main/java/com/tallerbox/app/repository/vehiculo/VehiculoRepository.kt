package com.tallerbox.app.repository.vehiculo

import com.tallerbox.app.model.vehiculo.VehiculoDao
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import kotlinx.coroutines.flow.Flow

class VehiculoRepository (private val dao: VehiculoDao){
    suspend fun insertar(vehiculo: VehiculoEntity) = dao.insertar(vehiculo)

    fun obtenerTodosFlow(): Flow<List<VehiculoEntity>> = dao.obtenerTodosFlow()

    fun obtenerPorClienteFlow(clienteId: Int): Flow<List<VehiculoEntity>> {
        return dao.obtenerPorClienteFlow(clienteId)
    }

    suspend fun obtenerVehiculoPorId(vehiculoId: Int): VehiculoEntity? = dao.obtenerVehiculoPorId(vehiculoId)

    suspend fun actualizarVehiculo(vehiculo: VehiculoEntity) {
        dao.actualizar(vehiculo)
    }

    suspend fun eliminar (vehiculo: VehiculoEntity) = dao.eliminar(vehiculo)
}