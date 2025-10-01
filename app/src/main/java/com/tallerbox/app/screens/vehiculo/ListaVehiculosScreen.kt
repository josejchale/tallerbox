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
fun ListaVehiculosScreen(navController: NavController, vm: VehiculoViewModel = viewModel(factory = VehiculoViewModelFactory(LocalContext.current))) {
    val vehiculo = vm.vehiculo.collectAsState().value

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top= 20.dp)) {

        Text("Vehiculos registrados", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        if (vehiculo.isEmpty()) {
            Text("No hay vehiculos registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vehiculo) { vehiculo ->
                    VehiculoCard(vehiculo = vehiculo, onClick = {
                        // ejemplo: navegar a detalle o registrar vehículo pasando igit d
                        navController.navigate("lista_vehiculo")
                    })
                }
            }
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