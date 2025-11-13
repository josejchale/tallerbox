package com.tallerbox.app.repository.cliente

import com.tallerbox.app.model.cliente.ClienteDao
import com.tallerbox.app.model.cliente.ClienteEntity
import kotlinx.coroutines.flow.Flow

class ClienteRepository (private val dao: ClienteDao){
    suspend fun insertar(cliente: ClienteEntity) = dao.insertar(cliente)

    suspend fun actualizarCliente(cliente: ClienteEntity) {
        dao.actualizar(cliente)
    }

    suspend fun obtenerClientePorId(idCliente: Int): ClienteEntity? {
        return dao.obtenerClientePorId(idCliente)
    }

    fun obtenerTodosFlow(): Flow<List<ClienteEntity>> = dao.obtenerTodosFlow()

    suspend fun eliminar(cliente: ClienteEntity) = dao.eliminar(cliente)
}