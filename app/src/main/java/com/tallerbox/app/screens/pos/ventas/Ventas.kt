package com.tallerbox.app.screens.pos.ventas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Ventas(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, top = 100.dp,),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ){
        Text("Ventas", style = MaterialTheme.typography.titleMedium)

    }
}