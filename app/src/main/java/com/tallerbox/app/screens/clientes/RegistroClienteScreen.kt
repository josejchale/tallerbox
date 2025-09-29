package com.tallerbox.app.screens.clientes
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.NavController

import com.tallerbox.app.model.cliente.ClienteEntity
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.viewmodel.cliente.ClienteViewModel
import com.tallerbox.app.viewmodel.cliente.ClienteViewModelFactory
import com.tallerbox.app.utils.estados
import com.tallerbox.app.utils.municipiosPorEstado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteScreen(navController: NavController, vm: ClienteViewModel = viewModel(factory = ClienteViewModelFactory(
    LocalContext.current
)
)) {    var nombreCompleto by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var numeroCasa by remember { mutableStateOf("") }
    var calle1 by remember { mutableStateOf("") }
    var calle2 by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf("") }
    var municipioSeleccionado by remember { mutableStateOf("") }

    var expandedEstado by remember { mutableStateOf(false) }
    var expandedMunicipio by remember { mutableStateOf(false) }

    val municipiosDisponibles = municipiosPorEstado[estadoSeleccionado] ?: emptyList()
    var telefono by remember { mutableStateOf("") }

    var errorTelefono by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val clienteDao = db.clienteDao()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registro de Cliente",
            style = MaterialTheme.typography.headlineSmall,

        )

        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { nombreCompleto = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calle,
            onValueChange = { calle = it },
            label = { Text("Calle") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = numeroCasa,
            onValueChange = { numeroCasa = it },
            label = { Text("Número de casa (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calle1,
            onValueChange = { calle1 = it },
            label = { Text("Cruzamientos") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calle2,
            onValueChange = { calle2 = it },
            label = { Text("Cruzamientos") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expandedEstado,
            onExpandedChange = { expandedEstado = !expandedEstado }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedEstado,
                onDismissRequest = { expandedEstado = false }
            ) {
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado) },
                        onClick = {
                            estadoSeleccionado = estado
                            municipioSeleccionado = "" // reset municipio
                            expandedEstado = false
                        }
                    )
                }
            }
        }

        if (estadoSeleccionado.isNotBlank()) {
            ExposedDropdownMenuBox(
                expanded = expandedMunicipio,
                onExpandedChange = { expandedMunicipio = !expandedMunicipio }
            ) {
                OutlinedTextField(
                    value = municipioSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Municipio") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedMunicipio,
                    onDismissRequest = { expandedMunicipio = false }
                ) {
                    municipiosDisponibles.forEach { municipio ->
                        DropdownMenuItem(
                            text = { Text(municipio) },
                            onClick = {
                                municipioSeleccionado = municipio
                                expandedMunicipio = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                errorTelefono = it.length != 10 || !it.all { c -> c.isDigit() }
            },
            label = { Text("Teléfono (10 dígitos)") },
            isError = errorTelefono,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (errorTelefono) {
            Text(
                text = "El teléfono debe tener 10 dígitos numéricos",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                if (!errorTelefono && nombreCompleto.isNotBlank()) {
                    val cliente = ClienteEntity(
                        nombreCompleto = nombreCompleto,
                        calle = calle,
                        numeroCasa = numeroCasa.ifBlank { null },
                        calle1 = calle1,
                        calle2 = calle2,
                        estado = estadoSeleccionado,
                        municipio = municipioSeleccionado,
                        telefono = telefono
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        clienteDao.insertar(cliente)

                    //Muestra un toast y navega al registro de vehiculos
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Cliente registrado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("registro_vehiculo")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cliente")
        }
    }
}
