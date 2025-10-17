package com.tallerbox.app.screens.orden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.tallerbox.app.repository.OrdenRepository
import com.tallerbox.app.viewmodel.orden.OrdenUiState
import com.tallerbox.app.viewmodel.orden.OrdenViewModel
import com.tallerbox.app.viewmodel.orden.OrdenViewModelFactory

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

    LaunchedEffect(vehiculoId, clienteId) {
        vm.setVehiculoFilter(vehiculoId)
        vm.setClienteFilter(if (vehiculoId == null) clienteId else null)
    }

    val state by vm.ordenesState.collectAsState()
    val scrollState = rememberScrollState() // 👈 permite desplazamiento

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Órdenes", style = MaterialTheme.typography.headlineSmall)

        when (state) {
            is OrdenUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is OrdenUiState.Error -> {
                Text(
                    (state as OrdenUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is OrdenUiState.SuccessList -> {
                val lista = (state as OrdenUiState.SuccessList).list

                if (lista.isEmpty()) {
                    Text(
                        "No hay órdenes para este vehículo o cliente",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    // Agrupamos las órdenes por estado (en minúsculas para evitar errores)
                    val grouped = lista.groupBy { it.estadoOrden?.lowercase() ?: "desconocido" }

                    // Definimos los colores según el estado
                    val estados = listOf(
                        "pendiente" to Color(0xFFFCC6BB), // rosa claro
                        "en proceso" to Color(0xFFFFEB69), // amarillo
                        "completada" to Color(0xFFA0FF69), // verde
                        "entregada" to Color(0xFFDBDBDB)   // gris claro
                    )

                    estados.forEach { (estado, color) ->
                        // 👇 Los estados "completada" y "entregada" empiezan cerrados
                        var expanded by remember {
                            mutableStateOf(estado !in listOf("completada", "entregada"))
                        }

                        val ordenesEstado = grouped[estado.lowercase()] ?: emptyList()

                        if (ordenesEstado.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column {
                                    // Encabezado con el nombre del estado y botón expandir/colapsar
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
                                            imageVector = if (expanded)
                                                Icons.Default.KeyboardArrowUp
                                            else
                                                Icons.Default.KeyboardArrowDown,
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
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                        .clickable {
                                                            navController.navigate("detalle_orden/${orden.id}")
                                                        },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = color
                                                    )
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            text = orden.numeroOrden,
                                                            style = MaterialTheme.typography.titleMedium
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "Fecha: ${orden.fechaIngreso}",
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(
                                                            text = orden.descripcionFalla ?: "",
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
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
    }
}
