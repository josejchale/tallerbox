package com.tallerbox.app.screens
import com.tallerbox.app.model.Cliente
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistroClienteScreen() {
    var nombreCompleto by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var numeroCasa by remember { mutableStateOf("") }
    var cruzamientos by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var errorTelefono by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registro de Cliente", style = MaterialTheme.typography.headlineSmall)

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
            value = cruzamientos,
            onValueChange = { cruzamientos = it },
            label = { Text("Cruzamientos") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado,
            onValueChange = { estado = it },
            label = { Text("Estado") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = municipio,
            onValueChange = { municipio = it },
            label = { Text("Municipio") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                errorTelefono = it.length != 10 || !it.all { c -> c.isDigit() }
            },
            label = { Text("Teléfono (10 dígitos)") },
            isError = errorTelefono,
            modifier = Modifier.fillMaxWidth()
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
                    val cliente = Cliente(
                        nombreCompleto,
                        calle,
                        numeroCasa.ifBlank { null },
                        cruzamientos,
                        estado,
                        municipio,
                        telefono
                    )
                    // Aquí puedes guardar el cliente o navegar
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cliente")
        }
    }
}
