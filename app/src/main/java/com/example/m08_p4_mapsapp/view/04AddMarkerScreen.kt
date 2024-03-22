package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddMarkerScreen(avm: APIViewModel, navController: NavController) {
    AddMarkerContent(avm, true, navController)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddMarkerContent(
    avm: APIViewModel,
    markerScreen: Boolean = false,
    navigationController: NavController
) {
    avm.modPrevScreen("AddMarkerScreen")
    val lat by avm.inputLat.observeAsState("")
    val long by avm.inputLong.observeAsState("")
    val name by avm.markerName.observeAsState("")
    val defaultIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val icon by avm.icon.observeAsState(defaultIcon)
    val photoTaken by avm.photoTaken.observeAsState(initial = false)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.run { if (markerScreen) Center else Top },
        ) {
            if (photoTaken) {
                GlideImage(
                    model = icon,
                    contentDescription = "Marker Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 10.dp)
                )
                Button(onClick = {
                    avm.switchBottomSheet(false)
                    avm.modMarcadorActual(lat.toDouble(), long.toDouble())
                    navigationController.navigate(Routes.CameraScreen.route)
                }) {
                    Text("RETAKE PICTURE")
                }
            } else {
                Button(onClick = {
                    avm.switchBottomSheet(false)
                    avm.modMarcadorActual(lat.toDouble(), long.toDouble())
                    navigationController.navigate(Routes.CameraScreen.route)
                }) {
                    Text("TAKE PICTURE")
                }
            }

            TextField(value = name,
                onValueChange = { avm.modMarkerName(it) },
                label = { Text("Nombre") })
            TextField(
                value = lat,
                onValueChange = {
                    if (it.toDoubleOrNull() != null) {
                        avm.modInputLat(it)
                    }
                },
                label = { Text("Latitud") },
            )
            TextField(
                value = long,
                onValueChange = {
                    if (it.toDoubleOrNull() != null) {
                        avm.modInputLong(it)
                    }
                },
                label = { Text("Longitud") },
            )
            val canAddMarker =
                photoTaken && name.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty()
            Button(onClick = {
                avm.addMarker(lat, long, name, icon)
                if (avm.prevScreen.value == "AddMarkerScreen") {
                    avm.switchBottomSheet(false)
                    navigationController.navigate(Routes.MapScreen.route)
                }
            }, enabled = canAddMarker) {
                Text("Agregar marcador")
            }

        }
    }
}
