package com.tallerbox.app.model.vehiculo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.tallerbox.app.model.cliente.ClienteEntity



@Entity(
    tableName = "vehiculos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
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
