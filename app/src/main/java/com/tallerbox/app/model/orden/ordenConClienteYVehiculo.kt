package com.tallerbox.app.model.orden


import androidx.room.Embedded
import androidx.room.Relation
import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.model.vehiculo.VehiculoEntity

data class OrdenConClienteYVehiculo(
    @Embedded val orden: OrdenServicioEntity,
    @Relation(parentColumn = "clienteId", entityColumn = "id")
    val cliente: ClienteEntity?,
    @Relation(parentColumn = "vehiculoId", entityColumn = "id")
    val vehiculo: VehiculoEntity?
)
