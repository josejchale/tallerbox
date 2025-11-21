package com.tallerbox.app.model.usuario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario WHERE id = 1")
    fun getUsuario(): Flow<UsuarioEntity?>

    @Insert
    suspend fun insert(usuario: UsuarioEntity)

    @Update
    suspend fun update(usuario: UsuarioEntity)

    @Query("SELECT firmaBase64 FROM usuario WHERE id = 1")
    fun getFirma(): Flow<String?>

}
