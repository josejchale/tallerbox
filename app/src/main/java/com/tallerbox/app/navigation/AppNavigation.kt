package com.tallerbox.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tallerbox.app.screens.MainScreen
import com.tallerbox.app.screens.RegistroClienteScreen
import com.tallerbox.app.screens.RegistroVehiculoScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("registro_cliente") {
            RegistroClienteScreen()
        }
        composable(route= "registro_vehiculo"){
            RegistroVehiculoScreen()
        }
    }
}
