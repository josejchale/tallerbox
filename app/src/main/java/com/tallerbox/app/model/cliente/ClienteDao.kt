package com.tallerbox.app.model.cliente

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert
    suspend fun insertar(cliente: ClienteEntity): Long

    @Update
    suspend fun actualizar(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes WHERE id = :clienteId LIMIT 1")
    suspend fun obtenerClientePorId(clienteId: Int): ClienteEntity?

    @Delete
    suspend fun eliminar(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes")
    fun obtenerTodosFlow(): Flow<List<ClienteEntity>>
}