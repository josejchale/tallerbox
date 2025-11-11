package com.tallerbox.app.repository.cliente

import com.tallerbox.app.model.cliente.ClienteDao
import com.tallerbox.app.model.cliente.ClienteEntity
import kotlinx.coroutines.flow.Flow

class ClienteRepository (private val dao: ClienteDao){
    suspend fun insertar(cliente: ClienteEntity) = dao.insertar(cliente)

    fun obtenerTodosFlow(): Flow<List<ClienteEntity>> = dao.obtenerTodosFlow()

    suspend fun eliminar(cliente: ClienteEntity) = dao.eliminar(cliente)
}