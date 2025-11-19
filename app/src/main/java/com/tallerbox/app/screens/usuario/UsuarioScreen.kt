package com.tallerbox.app.screens.usuario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun UsuarioPerfil(navController: NavHostController) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top=60.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text("Perfil de Usuario",
            style = MaterialTheme.typography.headlineSmall,

        )

    }
}