package com.tallerbox.app.screens.vehiculo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tallerbox.app.model.vehiculo.VehiculoEntity
import com.tallerbox.app.viewmodel.vehiculo.VehiculoViewModelFactory
import androidx.navigation.NavController
import com.tallerbox.app.viewmodel.vehiculo.VehiculoViewModel

@Composable
fun ListaVehiculosScreen(
    navController: NavController,
    clienteId: Int?,
    vm: VehiculoViewModel = viewModel(factory = VehiculoViewModelFactory(LocalContext.current))
) {
    val vehiculos = clienteId?.let {
        vm.obtenerVehiculosPorCliente(it).collectAsState().value
    } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top=60.dp)
            .padding(horizontal=20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Vehículos registrados", style = MaterialTheme.typography.headlineSmall)



        if (vehiculos.isEmpty()) {
            Text("Este cliente no tiene vehículos registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vehiculos) { vehiculo ->
                    VehiculoCard(vehiculo = vehiculo, onClick = {
                        // Aquí podrías navegar a detalle si lo implementas
                    })
                }
            }
        }
        Button(
            onClick = {
                clienteId?.let {
                    navController.navigate("registro_vehiculo/{clienteId}")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar nuevo vehículo")
        }
    }
}

@Composable
private fun VehiculoCard(vehiculo: VehiculoEntity, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "${vehiculo.marca} ${vehiculo.modelo}, ${vehiculo.ano}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Color: ${vehiculo.color}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "VIN: ${vehiculo.vin}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Placa: ${vehiculo.placa?: ""}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}