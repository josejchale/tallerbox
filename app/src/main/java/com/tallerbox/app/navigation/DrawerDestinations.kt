package com.tallerbox.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerDestination(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val drawerDestinations = listOf(
    DrawerDestination("Home", "main", Icons.Filled.Home),
    DrawerDestination("Clientes", "lista_clientes", Icons.Filled.Person),
    DrawerDestination("Vehículos", "lista_vehiculo", Icons.Filled.TwoWheeler),
    DrawerDestination("Ordenes de Servicio", "lista_ordenes", Icons.AutoMirrored.Filled.Assignment)
    )
