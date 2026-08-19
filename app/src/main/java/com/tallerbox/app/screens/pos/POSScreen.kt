package com.tallerbox.app.screens.pos

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun POSScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, top = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Button(onClick = {  }) {
                Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "ListAlt", modifier = Modifier.size(20.dp))
                //Spacer(modifier = Modifier.width(8.dp))
               // Text("Nuevo Cliente")
            }

            Button(onClick = {  }) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = "ListAlt", modifier = Modifier.size(20.dp))
                //Spacer(modifier = Modifier.width(8.dp))
                // Text("Nuevo Cliente")
            }

            Button(onClick = {  }) {
                Icon(Icons.Filled.Search, contentDescription = "ListAlt", modifier = Modifier.size(20.dp))
                //Spacer(modifier = Modifier.width(8.dp))
                // Text("Nuevo Cliente")
            }

            Button(onClick = {  }) {
                Icon(Icons.Filled.Search, contentDescription = "ListAlt", modifier = Modifier.size(20.dp))
                //Spacer(modifier = Modifier.width(8.dp))
                // Text("Nuevo Cliente")
            }

        }
    }
}