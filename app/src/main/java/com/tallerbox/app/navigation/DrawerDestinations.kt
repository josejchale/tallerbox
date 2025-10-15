package com.tallerbox.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerDestination(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val drawerDestinations = listOf(
    DrawerDestination("Vehículos", "lista_vehiculo", Icons.Filled.TwoWheeler),
    DrawerDestination("Clientes", "lista_clientes", Icons.Filled.Person),
    DrawerDestination("Ordenes de Servicio", "lista_ordenes", Icons.AutoMirrored.Filled.Assignment)
    )
