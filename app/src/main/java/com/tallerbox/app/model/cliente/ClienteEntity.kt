package com.tallerbox.app.model.cliente

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreCompleto: String,
    val calle: String,
    val numeroCasa: String?,
    val cruzamientos: String,
    val estado: String,
    val municipio: String,
    val telefono: String
)
