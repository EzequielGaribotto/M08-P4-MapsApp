package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddMarkerScreen(avm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val lat by avm.inputLat.observeAsState("")
        val long by avm.inputLong.observeAsState("")
        val selectedFile by avm.selectedFile.observeAsState("")
        val expanded by avm.expandedFile.observeAsState(false)
        Column {
            TextField(
                value = lat,
                onValueChange = { avm.modInputLat(it) },
                label = { Text("Latitud") }
            )
            TextField(
                value = long,
                onValueChange = { avm.modInputLong(it) },
                label = { Text("Longitud") }
            )
            Box(modifier = Modifier.padding(16.dp)) {
                Text("Seleccionar archivo: $selectedFile", modifier = Modifier.clickable { avm.switchExpandFile(true) })
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { avm.switchExpandFile(false) }
                ) {
                    DropdownMenuItem(onClick = {
                        avm.modSelectedFile("Archivo 1")
                        avm.switchExpandFile(false)
                    }) {
                        Text("Archivo 1")
                    }
                    DropdownMenuItem(onClick = {
                        avm.modSelectedFile("Archivo 2")
                        avm.switchExpandFile(false)
                    }) {
                        Text("Archivo 2")
                    }
                }
            }
        }
    }
}
