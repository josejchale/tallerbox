package com.tallerbox.app.screens.orden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.repository.OrdenRepository
import com.tallerbox.app.viewmodel.orden.OrdenViewModel
import com.tallerbox.app.viewmodel.orden.OrdenViewModelFactory

@Composable
fun ListaOrdenesScreen(navController: NavHostController, vehiculoId: Int? = null, clienteId: Int? = null) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repo = OrdenRepository(db.ordenServicioDao())
    val vm: OrdenViewModel = viewModel(factory = OrdenViewModelFactory(repo))

    // aplicar filtros: prioriza vehiculoId
    LaunchedEffect(vehiculoId, clienteId) {
        vm.setVehiculoFilter(vehiculoId)
        vm.setClienteFilter(if (vehiculoId == null) clienteId else null)
    }

    val state by vm.ordenesState.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 60.dp)
        .padding(bottom = 20.dp)
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Órdenes", style = MaterialTheme.typography.headlineSmall)

        when (state) {
            is com.tallerbox.app.viewmodel.orden.OrdenUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is com.tallerbox.app.viewmodel.orden.OrdenUiState.Error -> {
                Text((state as com.tallerbox.app.viewmodel.orden.OrdenUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            is com.tallerbox.app.viewmodel.orden.OrdenUiState.SuccessList -> {
                val lista = (state as com.tallerbox.app.viewmodel.orden.OrdenUiState.SuccessList).list
                if (lista.isEmpty()) {
                    Text("No hay órdenes para este vehículo", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(lista) { orden ->
                            Card(modifier = Modifier.fillMaxWidth().clickable {
                                navController.navigate("detalle_orden/${orden.id}")
                            }) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = orden.numeroOrden, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Fecha: ${orden.fechaIngreso}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = orden.descripcionFalla ?: "", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Volver")
            }
            Button(onClick = { navController.navigate("registro_orden") }, modifier = Modifier.weight(1f)) {
                Text("Nueva orden")
            }
        }
    }
}
