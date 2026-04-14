package com.tallerbox.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerDestination(
    val label: String,
    val route: String,
    val icon: ImageVector
)

data class DrawerSection(
    val title: String,
    val items: List<DrawerDestination>
)

val drawerSections = listOf(
    DrawerSection(
        title = "General",
        items = listOf(
            DrawerDestination("Home", "main", Icons.Filled.Home)
        )
    ),
    DrawerSection(
        title = "Servicios",
        items = listOf(
            DrawerDestination("Clientes", "lista_clientes", Icons.Filled.Person),
            DrawerDestination("Vehículos", "lista_vehiculo", Icons.Filled.TwoWheeler),
            DrawerDestination("Ordenes de Servicio", "lista_ordenes", Icons.AutoMirrored.Filled.Assignment)
        )
    ),
    DrawerSection(
        title = "Punto de Venta",
        items = listOf(
            DrawerDestination("Punto de Venta", "pos-screen", Icons.Filled.Store),
            DrawerDestination("Productos", "productos", Icons.Filled.QrCodeScanner),
            DrawerDestination("Inventario", "inventario", Icons.Filled.Inventory),
            DrawerDestination("Ventas", "ventas", Icons.Filled.Receipt),
            DrawerDestination("Ofertas", "ofertas", Icons.Filled.Percent)
        )
    )
)