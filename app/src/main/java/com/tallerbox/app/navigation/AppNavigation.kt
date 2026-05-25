package com.tallerbox.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tallerbox.app.screens.MainScreen

// Clientes
import com.tallerbox.app.screens.clientes.EditarClientesScreen
import com.tallerbox.app.screens.clientes.ListaClientesScreen
import com.tallerbox.app.screens.clientes.RegistroClienteScreen

// Vehículos
import com.tallerbox.app.screens.vehiculo.EditarVehiculoScreen
import com.tallerbox.app.screens.vehiculo.ListaVehiculosScreen
import com.tallerbox.app.screens.vehiculo.RegistroVehiculoScreen

// Órdenes
import com.tallerbox.app.screens.orden.RegistroOrdenScreen
import com.tallerbox.app.screens.orden.ListaOrdenesScreen
import com.tallerbox.app.screens.orden.DetalleOrdenScreen
import com.tallerbox.app.screens.orden.EditarOrdenScreen
import com.tallerbox.app.screens.usuario.UsuarioPerfil

//POS
import com.tallerbox.app.screens.pos.POSScreen
import com.tallerbox.app.screens.pos.productos.Productos
import com.tallerbox.app.screens.pos.inventario.Inventario
import com.tallerbox.app.screens.pos.ventas.Ventas
import com.tallerbox.app.screens.pos.ofertas.Ofertas



@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {

        // -----------------------------------------------------------
        // MAIN
        // -----------------------------------------------------------
        composable("main") {
            MainScreen(navController)
        }

        // -----------------------------------------------------------
        // PERFIL DEL USUARIO
        // -----------------------------------------------------------

        composable("usuario_perfil"){
            UsuarioPerfil()
        }

        // -----------------------------------------------------------
        // CLIENTES
        // -----------------------------------------------------------
        composable("registro_cliente") {
            RegistroClienteScreen(navController)
        }

        composable("lista_clientes") {
            ListaClientesScreen(navController)
        }

        composable("editar_cliente/{idCliente}") { backStackEntry ->
            val idCliente = backStackEntry.arguments?.getString("idCliente")?.toInt() ?: 0
            EditarClientesScreen(navController, idCliente)
        }

        // -----------------------------------------------------------
        // VEHÍCULOS
        // -----------------------------------------------------------
        composable("registro_vehiculo") {
            RegistroVehiculoScreen(navController, clienteId = null)
        }

        composable("registro_vehiculo/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            RegistroVehiculoScreen(navController, clienteId)
        }

        composable("lista_vehiculo") {
            ListaVehiculosScreen(navController, clienteId = null)
        }

        composable("lista_vehiculo/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            ListaVehiculosScreen(navController, clienteId)
        }

        composable("editar_vehiculo/{idVehiculo}") { backStackEntry ->
            val idVehiculo = backStackEntry.arguments?.getString("idVehiculo")?.toInt() ?: 0
            EditarVehiculoScreen(navController, vehiculoId = idVehiculo)
        }

        // -----------------------------------------------------------
        // ÓRDENES
        // -----------------------------------------------------------

        // Registro de orden
        composable("registro_orden") {
            RegistroOrdenScreen(navController, clienteId = null, vehiculoId = null)
        }

        composable("registro_orden/{clienteId}/{vehiculoId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId")?.toIntOrNull()
            RegistroOrdenScreen(navController, clienteId, vehiculoId)
        }

        // Lista de órdenes
        composable("lista_ordenes") {
            ListaOrdenesScreen(navController)
        }

        composable("lista_ordenes_cliente/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            ListaOrdenesScreen(navController, clienteId = clienteId)
        }

        composable("lista_ordenes_vehiculo/{vehiculoId}") { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId")?.toIntOrNull()
            ListaOrdenesScreen(navController, vehiculoId = vehiculoId)
        }

        // Detalle de orden
        composable("detalle_orden/{ordenId}") { backStackEntry ->
            val ordenId = backStackEntry.arguments?.getString("ordenId")?.toIntOrNull()
            DetalleOrdenScreen(navController, ordenId = ordenId)
        }

        // -----------------------------------------------------------
        // NUEVO COMPONENTE: EDITAR ORDEN
        composable("editar_orden/{ordenId}") { backStackEntry ->
            val ordenId = backStackEntry.arguments
                ?.getString("ordenId")
                ?.toInt()
                ?: error("ordenId es obligatorio en la ruta")

            EditarOrdenScreen(navController, ordenId)
        }

        //POS

        //Vista principal del POS
        composable ("pos-screen"){
            POSScreen()
        }

        composable ("productos"){
            Productos()
        }

        composable ("inventario"){
            Inventario()
        }

        composable ("ofertas"){
            Ofertas()
        }

        composable ("ventas"){
            Ventas()
        }

    }
}
