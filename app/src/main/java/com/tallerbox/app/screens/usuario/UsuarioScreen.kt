package com.tallerbox.app.screens.usuario

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tallerbox.app.components.firma_pad.FirmaPad
import com.tallerbox.app.db.AppDatabase
import com.tallerbox.app.helper.parseFirma
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModel
import com.tallerbox.app.viewmodel.usuario.UsuarioViewModelFactory
import com.tallerbox.app.repository.usuario.UsuarioRepository
import androidx.compose.material.icons.filled.Check

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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editable = !editable }
            ) {
                Icon(
                    imageVector = if (editable) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = "Editar"
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

        // ---------------- CAMPO: Nombre ----------------
        if (editable) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text(
                        "Nombre",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                readOnly = false,
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Nombre", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }


        Text("Firma:")

// ----------------- Mostrar firma si existe -----------------
        firmaBase64?.let { b64 ->
            val bmp = parseFirma(b64)?.let { firma ->
                try {
                    val bytes = Base64.decode(firma.base64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (e: Exception) {
                    Log.e("UsuarioPerfil", "Error decodificando firma", e)
                    null
                }
            }

            bmp?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(280.dp)
                            .height(140.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = MaterialTheme.shapes.medium,
                                clip = false
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Firma",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

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
}

