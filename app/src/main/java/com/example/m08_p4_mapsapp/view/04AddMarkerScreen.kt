package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddMarkerScreen(avm: APIViewModel, navController: NavController) {
    avm.modPrevScreen("AddMarkerScreen")
    AddMarkerContent(avm, true, navController)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddMarkerContent(
    avm: APIViewModel,
    markerScreen: Boolean = false,
    navigationController: NavController
) {
    val lat by avm.inputLat.observeAsState("")
    val long by avm.inputLong.observeAsState("")
    val name by avm.markerName.observeAsState("")
    val context = LocalContext.current
    val img: Bitmap = ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
    val icon by avm.icon.observeAsState(img)
    val url by avm.url.observeAsState("")
    val photoTaken = if (url =="") !icon.sameAs(img) else true

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.run { if (markerScreen) Center else Top },
        ) {
            GlideImage(
                model = if (url == "") icon else url,
                contentDescription = "Marker Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 10.dp)
            )
            Button(onClick = {
                avm.switchBottomSheet(false)
                avm.modMarcadorActual(
                    if (lat.isNotEmpty()) lat.toDouble() else (0.0),
                    if (long.isNotEmpty()) long.toDouble() else (0.0)
                )
                navigationController.navigate(Routes.CameraScreen.route)
            }) {
                Text((if (photoTaken) "RE" else "") + "TAKE PICTURE")
            }
            Button(onClick = {
                avm.switchBottomSheet(false)
                avm.modMarcadorActual(
                    if (lat.isNotEmpty()) lat.toDouble() else (0.0),
                    if (long.isNotEmpty()) long.toDouble() else (0.0)
                )
                navigationController.navigate(Routes.GalleryScreen.route)
            }) {
                Text("SELECT PICTURE FROM GALLERY")
            }

            TextField(value = name,
                onValueChange = { avm.modMarkerName(it) },
                label = { Text("Nombre") })
            TextField(
                value = lat,
                onValueChange = {
                    avm.modInputLat(it)
                },
                label = { Text("Latitud") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            TextField(
                value = long,
                onValueChange = {
                    avm.modInputLong(it)
                },
                label = { Text("Longitud") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            val canAddMarker =
                photoTaken && name.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty()
            Button(onClick = {
                avm.addMarker(lat, long, name, icon, url)
                avm.uploadImage(url.toUri())
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
