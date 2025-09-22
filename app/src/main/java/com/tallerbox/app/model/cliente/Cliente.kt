package com.tallerbox.app.model.cliente

data class Cliente(
    val nombreCompleto: String,
    val calle: String,
    val numeroCasa: String?,
    val cruzamientos: String,
    val estado: String,
    val municipio: String,
    val telefono: String
)