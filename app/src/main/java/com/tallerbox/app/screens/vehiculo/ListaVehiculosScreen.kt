package com.tallerbox.app.screens.vehiculo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.launch

@Composable
fun ListaVehiculosScreen(
    navController: NavController,
    clienteId: Int?,
    vm: VehiculoViewModel = viewModel(factory = VehiculoViewModelFactory(LocalContext.current))
) {
    val vehiculosState = produceState<List<VehiculoEntity>?>(initialValue = null, key1 = clienteId, key2 = vm) {
        if (clienteId == null) {
            vm.vehiculo.collect { lista -> value = lista }
        } else {
            vm.obtenerVehiculosPorCliente(clienteId).collect { lista -> value = lista }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Vehículos registrados", style = MaterialTheme.typography.headlineSmall)

        when (val lista = vehiculosState.value) {
            null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                if (lista.isEmpty()) {
                    Text(
                        if (clienteId == null)
                            "No hay vehículos registrados"
                        else
                            "Este cliente no tiene vehículos registrados",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(lista) { vehiculo ->
                            VehiculoCard(
                                vehiculo = vehiculo,
                                onClick = {
                                    navController.navigate("lista_ordenes_vehiculo/${vehiculo.id}")
                                },
                                onEditar = {
                                    // navController.navigate("editar_vehiculo/${vehiculo.id}")
                                },
                                onEliminar = {
                                    vm.eliminarVehiculo(vehiculo)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Vehículo eliminado",
                                            actionLabel = "Deshacer",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            vm.insertarVehiculo(vehiculo)
                                        }
                                    }
                                }
                            )
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

        // 👇 SnackbarHost al final de la pantalla
        SnackbarHost(hostState = snackbarHostState)
    }
}


@Composable
private fun VehiculoCard(
    vehiculo: VehiculoEntity,
    onClick: () -> Unit,
    onEditar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "${vehiculo.marca} ${vehiculo.modelo}, ${vehiculo.ano}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Color: ${vehiculo.color}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "VIN: ${vehiculo.vin}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Placa: ${vehiculo.placa ?: ""}", style = MaterialTheme.typography.bodyMedium)
            }

            // Botón de opciones en la esquina superior derecha
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    offset = DpOffset(x = (-8).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    modifier = Modifier.size(18.dp)
                                )
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
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(18.dp)
                                )
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

    // Diálogo de confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Eliminar vehículo") },
            text = {
                Text("¿Seguro que deseas eliminar este vehículo? Se borrarán también sus órdenes asociadas.")
            },
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
