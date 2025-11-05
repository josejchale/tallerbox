package com.tallerbox.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tallerbox.app.screens.MainScreen
import com.tallerbox.app.screens.clientes.ListaClientesScreen
import com.tallerbox.app.screens.clientes.RegistroClienteScreen
import com.tallerbox.app.screens.orden.RegistroOrdenScreen
import com.tallerbox.app.screens.orden.ListaOrdenesScreen
import com.tallerbox.app.screens.orden.DetalleOrdenScreen
import com.tallerbox.app.screens.vehiculo.ListaVehiculosScreen
import com.tallerbox.app.screens.vehiculo.RegistroVehiculoScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }

        // Registro de cliente
        composable("registro_cliente") {
            RegistroClienteScreen(navController)
        }

        composable("lista_clientes") {
            ListaClientesScreen(navController)
        }

        // Registro de vehículo con clienteId (ruta con parámetro)
        composable("registro_vehiculo/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            RegistroVehiculoScreen(navController, clienteId)
        }

        // Registro de vehículo sin parámetro
        composable("registro_vehiculo") {
            RegistroVehiculoScreen(navController, clienteId = null)
        }

        // Lista de vehiculos sin parametros
        composable("lista_vehiculo") {
            ListaVehiculosScreen(navController, clienteId = null)
        }


        // Lista de vehículos filtrada por clienteId
        composable("lista_vehiculo/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            ListaVehiculosScreen(navController, clienteId)
        }

        // ----- Órdenes de servicio -----

        // Registrar orden: con clienteId y vehiculoId
        composable("registro_orden/{clienteId}/{vehiculoId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId")?.toIntOrNull()
            RegistroOrdenScreen(navController, clienteId = clienteId, vehiculoId = vehiculoId)
        }

        // Registrar orden sin parámetros
        composable("registro_orden") {
            RegistroOrdenScreen(navController, clienteId = null, vehiculoId = null)
        }

        // Lista de órdenes (todas)
        composable("lista_ordenes") {
            ListaOrdenesScreen(navController)
        }

// Lista de órdenes por cliente
        composable("lista_ordenes_cliente/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            ListaOrdenesScreen(navController, clienteId = clienteId)
        }

        // Detalle de orden por id
        composable("detalle_orden/{ordenId}") { backStackEntry ->
            val ordenId = backStackEntry.arguments?.getString("ordenId")?.toIntOrNull()
            DetalleOrdenScreen(navController, ordenId = ordenId)
        }

// Lista de órdenes por vehículo
        composable("lista_ordenes_vehiculo/{vehiculoId}") { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId")?.toIntOrNull()
            ListaOrdenesScreen(navController, vehiculoId = vehiculoId)
        }

    }
}
