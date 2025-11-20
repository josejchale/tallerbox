package com.tallerbox.app.model.usuario

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: Int = 1,
    val nombre: String,
    val firmaBase64: String? = null
)
