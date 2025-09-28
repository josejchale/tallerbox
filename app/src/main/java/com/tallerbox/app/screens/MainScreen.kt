package com.tallerbox.app.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Título superior
            Text(
                text = "TallerBox",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "¿Qué quieres hacer hoy?",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        // Navegar a Nueva Orden (futuro)
                    }) {
                        Text("Nueva Orden de Servicio")
                    }

                    Button(onClick = {
                        navController.navigate("registro_cliente")
                    }) {
                        Text("Registrar Cliente")
                    }

                    Button(onClick = {
                        // Navegar a Registro Vehículo (futuro)
                        navController.navigate(route = "registro_vehiculo")
                    }) {
                        Text("Registrar Vehículo")
                    }
                }
                Button(onClick={
                    navController.navigate(route="lista_clientes")
                }){
                    Text(text="ver lista de clientes")
                }
                Button(onClick={
                    navController.navigate(route="lista_vehiculo")
                }){
                    Text(text="ver lista de vehiculos")
                }
            }
        }
    }
}
