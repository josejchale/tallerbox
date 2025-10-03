package com.tallerbox.app.screens.orden

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.repository.OrdenRepository
import com.tallerbox.app.viewmodel.orden.OrdenViewModel
import com.tallerbox.app.viewmodel.orden.OrdenViewModelFactory

@Composable
fun DetalleOrdenScreen(navController: NavHostController, ordenId: Int?) {
    val context = LocalContext.current
    if (ordenId == null) {
        Text("Orden no especificada", color = MaterialTheme.colorScheme.error)
        return
    }

    val db = AppDatabase.getDatabase(context)
    val repo = OrdenRepository(db.ordenServicioDao())
    val vm: OrdenViewModel = viewModel(factory = OrdenViewModelFactory(repo))

    val detalleResult by vm.detalle.collectAsState()
    LaunchedEffect(ordenId) { vm.cargarDetalle(ordenId) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 60.dp).padding(bottom = 20.dp)
        .padding(bottom = 10.dp)
        .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when {
            detalleResult.isSuccess && detalleResult.getOrNull() == null -> {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            detalleResult.isFailure -> {
                Text("Error al cargar la orden", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                val data: OrdenConClienteYVehiculo? = detalleResult.getOrNull()
                if (data == null) {
                    Text("Orden no encontrada", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("Orden: ${data.orden.numeroOrden}", style = MaterialTheme.typography.headlineSmall)
                    Text("Cliente: ${data.cliente?.nombreCompleto ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Vehículo: ${data.vehiculo?.marca ?: "—"} ${data.vehiculo?.modelo ?: ""}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Falla: ${data.orden.descripcionFalla ?: ""}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trabajo realizado: ${data.orden.trabajoRealizado ?: ""}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Costos: ${data.orden.costos.costo}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Volver")
            }
            Button(onClick = {
                Toast.makeText(context, "Función editar pendiente", Toast.LENGTH_SHORT).show()
            }, modifier = Modifier.weight(1f)) {
                Text("Editar")
            }
        }
    }
}
