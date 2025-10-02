package com.tallerbox.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tallerbox.app.screens.MainScreen
import com.tallerbox.app.screens.clientes.ListaClientesScreen
import com.tallerbox.app.screens.clientes.RegistroClienteScreen
import com.tallerbox.app.screens.vehiculo.ListaVehiculosScreen
import com.tallerbox.app.screens.vehiculo.RegistroVehiculoScreen
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("registro_cliente") {
            RegistroClienteScreen(navController)
        }

        composable("lista_clientes") {
            ListaClientesScreen(navController)
        }
        composable("registro_vehiculo/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            RegistroVehiculoScreen(navController, clienteId)
        }

        composable(route="registro_vehiculo"){
            val clienteId = null
            RegistroVehiculoScreen(navController, clienteId)
        }

        composable("lista_vehiculo/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull()
            ListaVehiculosScreen(navController, clienteId)
        }

    }
}
