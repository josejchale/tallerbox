package com.tallerbox.app.model

data class Cliente(
    val nombreCompleto: String,
    val calle: String,
    val numeroCasa: String?,
    val cruzamientos: String,
    val estado: String,
    val municipio: String,
    val telefono: String
)
