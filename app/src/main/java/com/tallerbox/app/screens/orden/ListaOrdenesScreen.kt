package com.tallerbox.app.screens.orden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.model.orden.OrdenServicioEntity
import com.tallerbox.app.repository.OrdenRepository
import com.tallerbox.app.viewmodel.orden.OrdenViewModel
import com.tallerbox.app.viewmodel.orden.OrdenViewModelFactory
import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.launch

@Composable
fun ListaOrdenesScreen(
    navController: NavHostController,
    vehiculoId: Int? = null,
    clienteId: Int? = null
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repo = OrdenRepository(db.ordenServicioDao())
    val vm: OrdenViewModel = viewModel(factory = OrdenViewModelFactory(repo))


    val ordenesFlow by remember(vehiculoId, clienteId) {
        mutableStateOf(
            when {
                vehiculoId != null -> vm.obtenerPorVehiculo(vehiculoId)
                clienteId != null -> vm.obtenerPorCliente(clienteId)
                else -> vm.ordenes
            }
        )
    }

    val ordenes by ordenesFlow.collectAsState(initial = emptyList())
    val isLoading = ordenes.isEmpty() && ordenesFlow.collectAsState(initial = emptyList()).value.isNotEmpty()

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Órdenes", style = MaterialTheme.typography.headlineSmall)

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            ordenes.isEmpty() -> {
                Text("No hay órdenes disponibles", style = MaterialTheme.typography.bodyMedium)
            }

            else -> {
                val grouped = ordenes.groupBy { it.estadoOrden?.lowercase() ?: "desconocido" }

                val estados = listOf(
                    "pendiente" to Color(0xFF73FFEF),
                    "en proceso" to Color(0xFFFFE23A),
                    "completada" to Color(0xFF6BFF14),
                    "entregada" to Color(0xFFDBDBDB),
                    "cancelado" to Color(0xFFFF5767)
                )

                estados.forEach { (estado, color) ->
                    var expanded by remember {
                        mutableStateOf(estado !in listOf("completada", "entregada", "cancelado"))
                    }

                    val ordenesEstado = grouped[estado.lowercase()] ?: emptyList()

                    if (ordenesEstado.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expanded = !expanded }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        estado.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                }

                                if (expanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        ordenesEstado.forEach { orden ->
                                            OrdenCard(
                                                orden = orden,
                                                color = color,
                                                onClick = {
                                                    navController.navigate("detalle_orden/${orden.id}")
                                                },
                                                onEditar = {
                                                    // navController.navigate("editar_orden/${orden.id}")
                                                },
                                                onEliminar = {
                                                    vm.eliminar(orden)
                                                    scope.launch {
                                                        val result = snackbarHostState.showSnackbar(
                                                            message = "Orden eliminada",
                                                            actionLabel = "Deshacer",
                                                            duration = SnackbarDuration.Short
                                                        )
                                                        if (result == SnackbarResult.ActionPerformed) {
                                                            vm.insertar(orden)
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Volver")
            }
            Button(
                onClick = {
                    navController.navigate("registro_orden/$clienteId/${vehiculoId}")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Nueva orden")
            }
        }

        // 👇 SnackbarHost al final
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun OrdenCard(
    orden: OrdenServicioEntity,
    color: Color,
    onClick: () -> Unit,
    onEditar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = orden.numeroOrden, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Fecha: ${orden.fechaIngreso}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = orden.descripcionFalla ?: "", style = MaterialTheme.typography.bodyMedium)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opciones")
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    offset = DpOffset(x = (-8).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Editar")
                            }
                        },
                        onClick = {
                            menuExpanded = false
                            onEditar()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Eliminar")
                            }
                        },
                        onClick = {
                            menuExpanded = false
                            mostrarDialogo = true
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Eliminar orden") },
            text = { Text("¿Seguro que deseas eliminar la orden ${orden.numeroOrden}?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    onEliminar()
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
