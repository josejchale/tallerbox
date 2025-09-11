package com.tallerbox.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tallerbox.app.ui.theme.TallerBoxTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TallerBoxTheme {
                // Pantalla principal
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "TallerBox",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "¿Qué quieres hacer hoy?",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { /* Nueva Orden */ }) { Text("Nueva Orden de Servicio") }
                    Button(onClick = { /* Registrar Cliente */ }) { Text("Registrar Cliente") }
                    Button(onClick = { /* Registrar Vehículo */ }) { Text("Registrar Vehículo") }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TallerBoxTheme {
        MainScreen()
    }
}
