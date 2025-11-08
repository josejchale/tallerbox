package com.tallerbox.app.screens.clientes

import android.app.Application
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpOffset
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color


@Composable
fun ListaClientesScreen(navController: NavController, vm: ClienteViewModel = viewModel(factory = ClienteViewModelFactory(LocalContext.current.applicationContext as Application))) {
    val clientes = vm.clientes.collectAsState().value

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top=60.dp)
        .padding(horizontal=20.dp),
    ) {

        Text("Clientes registrados", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        if (clientes.isEmpty()) {
            Text("No hay clientes registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(clientes) { cliente ->
                    ClienteCard(
                        cliente = cliente,
                        onClick = {
                            navController.navigate("lista_vehiculo/${cliente.id}")
                        },
                        onEditar = {
                        //navController.navigate("editar_cliente/${cliente.id}")
                        },
                        onEliminar = {
                        //vm.eliminarCliente(cliente)
                        }
                    )

                }
            }
        }
        Button(
            onClick = {
                navController.navigate("registro_cliente")

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=10.dp)
        ) {
            Text("Registrar nuevo cliente")
        }
    }
}

@Composable
private fun ClienteCard(
    cliente: ClienteEntity,
    onClick: () -> Unit,
    onEditar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = cliente.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Tel: ${cliente.telefono}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Calle ${cliente.calle}, ${cliente.numeroCasa ?: ""}, entre calles ${cliente.calle1} y ${cliente.calle2}, ${cliente.municipio}, ${cliente.estado}",
                    style = MaterialTheme.typography.bodySmall
                )
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
                            onEliminar()
                        }
                    )
                }
            }
        }
    }
}
