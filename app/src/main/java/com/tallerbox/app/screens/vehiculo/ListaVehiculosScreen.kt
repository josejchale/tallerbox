package com.tallerbox.app.screens.vehiculo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
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
    // estado nullable: null = esperando primera emisión, List = datos recibidos
    val vehiculosState = produceState<List<VehiculoEntity>?>(initialValue = null, key1 = clienteId, key2 = vm) {
        if (clienteId == null) {
            value = emptyList()
            return@produceState
        }
        vm.obtenerVehiculosPorCliente(clienteId).collect { lista ->
            value = lista
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Vehículos registrados", style = MaterialTheme.typography.headlineSmall)

        // esperar la primera emisión
        when (val lista = vehiculosState.value) {
            null -> {
                // indicador de carga mientras esperamos la primera emisión
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                if (lista.isEmpty()) {
                    Text("Este cliente no tiene vehículos registrados", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(lista) { vehiculo ->
                            VehiculoCard(vehiculo = vehiculo, onClick = {
                                // navegar a registro de orden pasando clienteId y vehiculoId
                                // asumimos clienteId no nulo porque la pantalla fue abierta con un cliente
                                val cId = clienteId ?: 0
                               // navController.navigate("registro_orden/$cId/${vehiculo.id}")
                                navController.navigate("lista_ordenes/${vehiculo.id}")
                            })
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (clienteId != null) {
                    navController.navigate("registro_vehiculo/$clienteId")
                } else {
                    navController.navigate("registro_vehiculo")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
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
            Text(text = "Placa: ${vehiculo.placa ?: ""}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
