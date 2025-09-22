package com.tallerbox.app.model.cliente

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ClienteDao {
    @Insert
    suspend fun insertar(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes")
    suspend fun obtenerTodos(): List<ClienteEntity>
}