package com.tallerbox.app.screens.usuario

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tallerbox.app.components.firma_pad.FirmaPad
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModel
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModelFactory
import com.tallerbox.app.repository.usuario.UsuarioRepository

@Composable
fun UsuarioPerfil() {

    // ----------- Tu creación manual del Repo y VM (SE MANTIENE) ------------
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repo = UsuarioRepository(db.usuarioDao())

    val vm: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(repo)
    )
    // ------------------------------------------------------------------------

    val usuario by vm.usuario.collectAsState()

    var editable by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var firmaBase64 by remember { mutableStateOf<String?>(null) }

    var mostrarFirmaDialog by remember { mutableStateOf(false) }

    // Cargar datos REALES del usuario cuando cambia en BD
    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            firmaBase64 = it.firmaBase64
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Perfil de Usuario", style = MaterialTheme.typography.headlineSmall)

            IconButton(onClick = { editable = !editable }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }

        // ---------------- CAMPO: Nombre ----------------
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            enabled = editable,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Firma:")

        // ----------------- Mostrar firma si existe -----------------
        firmaBase64?.let { b64 ->
            val bytes = Base64.decode(b64, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Firma",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }

        // ------------- Botón para abrir FirmaPad en Dialog --------------
        if (editable) {
            Button(onClick = { mostrarFirmaDialog = true }) {
                Text("Actualizar Firma")
            }
        }

        // ======================= BOTONES =======================

        if (editable) {

            // ----------- SI NO EXISTE USUARIO → CREAR -------------
            if (usuario == null) {
                Button(
                    onClick = {
                        vm.crear(nombre, firmaBase64)

                        // getUsuario() actualiza solo porque es Flow
                        editable = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Usuario")
                }
            }

            // ----------- SI YA EXISTE → ACTUALIZAR ----------------
            else {
                Button(
                    onClick = {
                        vm.actualizar(
                            usuario!!.copy(
                                nombre = nombre,
                                firmaBase64 = firmaBase64
                            )
                        )
                        editable = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
            }
        }
    }

    // ----------- Dialog con FirmaPad -----------
    if (mostrarFirmaDialog) {
        AlertDialog(
            onDismissRequest = { mostrarFirmaDialog = false },
            confirmButton = {},
            text = {
                FirmaPad(onFirmaConfirmada = { nuevaFirma ->
                    firmaBase64 = nuevaFirma
                    mostrarFirmaDialog = false

                    if (usuario == null) {
                        vm.crear(nombre, nuevaFirma)
                        Log.d("UsuarioPerfil", "Firma guardada: $nuevaFirma")

                    } else {
                        vm.actualizar(usuario!!.copy(firmaBase64 = nuevaFirma))
                        Log.d("UsuarioPerfil", "Firma guardada: $nuevaFirma")

                    }
                })

            }
        )
    }
}
