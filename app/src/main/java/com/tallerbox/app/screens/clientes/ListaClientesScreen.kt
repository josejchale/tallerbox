package com.tallerbox.app.screens.clientes

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
import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.viewmodel.cliente.ClienteViewModel
import com.tallerbox.app.viewmodel.cliente.ClienteViewModelFactory
import androidx.navigation.NavController

@Composable
fun ListaClientesScreen(navController: NavController, vm: ClienteViewModel = viewModel(factory = ClienteViewModelFactory(LocalContext.current))) {
    val clientes = vm.clientes.collectAsState().value

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Clientes registrados", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        if (clientes.isEmpty()) {
            Text("No hay clientes registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(clientes) { cliente ->
                    ClienteCard(cliente = cliente, onClick = {
                        // ejemplo: navegar a detalle o registrar vehículo pasando id
                        navController.navigate("registro_vehiculo/${cliente.id}")
                    })
                }
            }
        }
    }
}

@Composable
private fun ClienteCard(cliente: ClienteEntity, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = cliente.nombreCompleto, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Tel: ${cliente.telefono}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "${cliente.calle} ${cliente.numeroCasa ?: ""}, ${cliente.municipio}, ${cliente.estado}", style = MaterialTheme.typography.bodySmall)
        }
    }
}