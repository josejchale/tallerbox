package com.tallerbox.app.model.cliente

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert
    suspend fun insertar(cliente: ClienteEntity): Long

    @Query("SELECT * FROM clientes")
    fun obtenerTodosFlow(): Flow<List<ClienteEntity>>
}