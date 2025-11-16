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
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.repository.orden.OrdenRepository
import com.tallerbox.app.utils.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetalleOrdenScreen(navController: NavHostController, ordenId: Int?) {
    val context = LocalContext.current

    if (ordenId == null) {
        Text("Orden no especificada", color = MaterialTheme.colorScheme.error)
        return
    }

    val db = AppDatabase.getDatabase(context)
    val repo = OrdenRepository(db.ordenServicioDao())

    // 🔹 Estado local para manejar la carga de datos
    var detalleResult by remember { mutableStateOf<Result<OrdenConClienteYVehiculo?>?>(null) }
    val scope = rememberCoroutineScope()

    // 🔹 Cargar detalle al iniciar
    LaunchedEffect(ordenId) {
        scope.launch(Dispatchers.IO) {
            try {
                val data = repo.obtenerOrdenConRelaciones(ordenId)
                detalleResult = Result.success(data)
            } catch (e: Exception) {
                detalleResult = Result.failure(e)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            when {
                detalleResult == null -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                detalleResult!!.isFailure -> {
                    Text("Error al cargar la orden", color = MaterialTheme.colorScheme.error)
                }

                detalleResult!!.isSuccess -> {
                    val data = detalleResult!!.getOrNull()
                    if (data == null) {
                        Text("Orden no encontrada", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        TicketOrdenDetalle(data, ordenId, navController) {
                            // Recargar después de actualizar estado
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val recarga = repo.obtenerOrdenConRelaciones(ordenId)
                                    detalleResult = Result.success(recarga)
                                } catch (e: Exception) {
                                    detalleResult = Result.failure(e)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("main") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Volver")
                }

                val contextForPdf = LocalContext.current
                val scopeForPdf = rememberCoroutineScope()

                Button(
                    onClick = {
                        val data = detalleResult?.getOrNull()
                        if (data == null) {
                            Toast.makeText(contextForPdf, "Orden no cargada", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scopeForPdf.launch(Dispatchers.IO) {
                            try {
                                val filename = "orden_${data.orden.numeroOrden}.pdf"
                                val outFile = File(contextForPdf.cacheDir, filename)
                                PdfGenerator.generateOrdenPdf(contextForPdf, data, outFile)

                                val authority = "${contextForPdf.packageName}.fileprovider"
                                val uri = FileProvider.getUriForFile(contextForPdf, authority, outFile)

                                launch(Dispatchers.Main) {
                                    showShareOptions(contextForPdf, uri)
                                }
                            } catch (e: Exception) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        contextForPdf,
                                        "Error generando PDF: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Enviar PDF")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketOrdenDetalle(
    data: OrdenConClienteYVehiculo,
    ordenId: Int,
    navController: NavHostController,
    onEstadoActualizado: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val ordenDao = db.ordenServicioDao()
    val scope = rememberCoroutineScope()

    val formatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
    }

    var estadoOrden by remember { mutableStateOf(data.orden.estadoOrden ?: "PENDIENTE") }
    var estadoExpanded by remember { mutableStateOf(false) }
    val esCompletada = estadoOrden == "COMPLETADA"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Orden #${data.orden.numeroOrden}", style = MaterialTheme.typography.titleLarge)
            HorizontalDivider()

            Text("Cliente: ${data.cliente?.nombreCompleto ?: "—"}")
            Text("Vehículo: ${data.vehiculo?.marca ?: "—"} ${data.vehiculo?.modelo ?: ""}")

            Text("Estado de la orden", style = MaterialTheme.typography.labelMedium)

            ExposedDropdownMenuBox(
                expanded = estadoExpanded,
                onExpandedChange = { estadoExpanded = it }
            ) {
                OutlinedTextField(
                    value = estadoOrden,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = estadoExpanded,
                    onDismissRequest = { estadoExpanded = false }
                ) {
                    listOf("PENDIENTE", "EN PROCESO", "COMPLETADA", "ENTREGADA", "CANCELADO").forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                estadoOrden = estado
                                estadoExpanded = false

                                // Si se selecciona "COMPLETADA", guardar automáticamente
                                if (estado == "ENTREGADA") {
                                    val fechaEntregaReal = Date()
                                    scope.launch(Dispatchers.IO) {
                                        ordenDao.actualizarEstadoYEntrega(ordenId, estado, fechaEntregaReal)
                                        launch(Dispatchers.Main) {
                                            Toast.makeText(context, "Orden marcada como ENTREGADA", Toast.LENGTH_SHORT).show()
                                            onEstadoActualizado()
                                        }
                                    }
                                }
                            }
                        )

                    }
                }
            }

            if (estadoOrden != "ENTREGADA") {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            ordenDao.actualizarEstadoYEntrega(ordenId, estadoOrden, null)
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
                                onEstadoActualizado()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar estado")
                }
            }


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

            val mitad = listaCondiciones.size / 2
            val columnaIzq = listaCondiciones.subList(0, mitad)
            val columnaDer = listaCondiciones.subList(mitad, listaCondiciones.size)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    columnaIzq.forEach { (nombre, estado) ->
                        Text("$nombre: ${estado.name}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    columnaDer.forEach { (nombre, estado) ->
                        Text("$nombre: ${estado.name}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (!condiciones.observaciones.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Observaciones: ${condiciones.observaciones}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider()

            Text(
                "Costo total: $${data.orden.costos.costo}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
