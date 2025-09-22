package com.tallerbox.app.model.vehiculo

data class Vehiculo(
    val marca: String,
    val modelo: String,
    val ano: String,
    val color: String,
    val vin: String,
    val placa: String?,
)