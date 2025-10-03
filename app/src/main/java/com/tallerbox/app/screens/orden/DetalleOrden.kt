package com.tallerbox.app.screens.orden

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.text.SimpleDateFormat
import java.util.Locale

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            when {
                detalleResult.isSuccess && detalleResult.getOrNull() == null -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                detalleResult.isFailure -> {
                    Text("Error al cargar la orden", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    val data = detalleResult.getOrNull()
                    if (data == null) {
                        Text("Orden no encontrada", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        TicketOrdenDetalle(data)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate("main") }, modifier = Modifier.weight(1f)) {
                    Text("Terminar")
                }
                Button(onClick = {
                    Toast.makeText(context, "Función enviar pendiente", Toast.LENGTH_SHORT).show()
                }, modifier = Modifier.weight(1f)) {
                    Text("Enviar PDF")
                }
            }
        }
    }



}

@Composable
fun TicketOrdenDetalle(data: OrdenConClienteYVehiculo) {
    val formatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")) // aún funcional, pero puedes usar:
        // SimpleDateFormat("dd MMM yyyy", Locale.Builder().setLanguage("es").setRegion("MX").build())
    }


    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Orden #${data.orden.numeroOrden}", style = MaterialTheme.typography.titleLarge)
            HorizontalDivider()


            Text("Cliente: ${data.cliente?.nombreCompleto ?: "—"}")
            Text("Vehículo: ${data.vehiculo?.marca ?: "—"} ${data.vehiculo?.modelo ?: ""}")
            Text("Estado: ${data.orden.estadoOrden ?: "—"}", style = MaterialTheme.typography.labelLarge)

            HorizontalDivider()


            Text("Fecha ingreso: ${formatter.format(data.orden.fechaIngreso)}")
            Text("Entrega estimada: ${data.orden.fechaEntregaEstimado?.let { formatter.format(it) } ?: "—"}")
            Text("Entrega real: ${data.orden.fechaEntregaReal?.let { formatter.format(it) } ?: "—"}")

            HorizontalDivider()


            Text("Falla reportada:", style = MaterialTheme.typography.labelMedium)
            Text(data.orden.descripcionFalla ?: "—")

            Text("Trabajo realizado:", style = MaterialTheme.typography.labelMedium)
            Text(data.orden.trabajoRealizado ?: "—")

            HorizontalDivider()


            Text("Condiciones del vehículo:", style = MaterialTheme.typography.labelMedium)

            val condiciones = data.orden.condiciones
            val listaCondiciones = listOf(
                "Espejos" to condiciones.espejos,
                "Asientos" to condiciones.asientos,
                "Faro delantero" to condiciones.faroDelantero,
                "Luz trasera" to condiciones.luzTrasera,
                "Direccionales" to condiciones.direccionales,
                "Cubiertas" to condiciones.cubiertas,
                "Tapon gasolina" to condiciones.taponGasolina,
                "Pedales" to condiciones.pedales,
                "Parabrisas" to condiciones.parabrisas,
                "Claxon" to condiciones.claxon,
                "Tapon aceite" to condiciones.taponAceite,
                "Tapon radiador" to condiciones.taponRadiador,
                "Filtro aire" to condiciones.filtroAire,
                "Batería" to condiciones.bateria,
                "Llaves" to condiciones.llaves
            )

            // Divide la lista en dos columnas
            val mitad = listaCondiciones.size / 2
            val columnaIzq = listaCondiciones.subList(0, mitad)
            val columnaDer = listaCondiciones.subList(mitad, listaCondiciones.size)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    columnaIzq.forEach { (nombre, estado) ->
                        Text("$nombre: ${estado.name}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    columnaDer.forEach { (nombre, estado) ->
                        Text("$nombre: ${estado.name}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (!condiciones.observaciones.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Observaciones: ${condiciones.observaciones}", style = MaterialTheme.typography.bodyMedium)
            }


            HorizontalDivider()


            Text("Costo total: \$${data.orden.costos.costo}", style = MaterialTheme.typography.titleMedium)
        }
    }
}
