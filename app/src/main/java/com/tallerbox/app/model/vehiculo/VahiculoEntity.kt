package com.tallerbox.app.model.vehiculo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "vehiculos")
data class VehiculoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteId: Int,
    val marca: String,
    val modelo: String,
    val ano: String,
    val color: String,
    val vin: String,
    val placa: String?
)
