package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_p4_mapsapp.ClickOutsideToDismissKeyboard
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.ViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddMarkerScreen(vm: ViewModel, navController: NavController) {
    vm.modPrevScreen("AddMarkerScreen")
    AddMarkerContent(vm, true, navController)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddMarkerContent(
    vm: ViewModel,
    markerScreen: Boolean = false,
    navigationController: NavController
) {
    val lat by vm.inputLat.observeAsState("")
    val long by vm.inputLong.observeAsState("")
    val name by vm.markerName.observeAsState("")
    val context = LocalContext.current
    val img: Bitmap = ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
    val icon by vm.icon.observeAsState(img)
    val url by vm.url.observeAsState("")
    val photoTaken = if (url == "") !icon.sameAs(img) else true

    ClickOutsideToDismissKeyboard {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.run { if (markerScreen) Center else Top },
            ) {
                SetPhoto(url, icon, vm, lat, long, navigationController, photoTaken)
                SetData(name, vm, lat, long)
                AddMarker(photoTaken, name, lat, long, vm, icon, url, navigationController, context)
            }
        }
    }
}

@Composable
fun AddMarker(
    photoTaken: Boolean,
    name: String,
    lat: String,
    long: String,
    vm: ViewModel,
    icon: Bitmap,
    url: String,
    navigationController: NavController,
    context: Context
) {
    val canAddMarker =
        photoTaken && name.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty()
    Button(onClick = {
        vm.addMarker(lat, long, name, icon, url)
        vm.uploadImage(url.toUri())
        vm.showBottomSheet(false)

        if (vm.prevScreen.value == "AddMarkerScreen") {
            navigationController.navigate(Routes.MapScreen.route)
        } else {
            vm.resetMarkerValues(context)
        }
    }, enabled = canAddMarker) {
        Text("Agregar marcador")
    }
}

@Composable
fun SetData(
    name: String,
    vm: ViewModel,
    lat: String,
    long: String
) {
    SetName(name, vm)
    TextField(
        value = lat,
        onValueChange = { vm.modInputLat(it) },
        label = { Text("Latitud") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
    TextField(
        value = long,
        onValueChange = { vm.modInputLong(it) },
        label = { Text("Longitud") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
fun SetName(name: String, vm: ViewModel) {
    TextField(value = name,
        onValueChange = { vm.modMarkerName(it) },
        label = { Text("Nombre") })
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun SetPhoto(
    url: String?,
    icon: Bitmap?,
    vm: ViewModel,
    lat: String,
    long: String,
    navigationController: NavController,
    photoTaken: Boolean
) {
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    println("PEPE PEREZ SANTOS"+url)
    GlideImage(
        model = if (url.isNullOrBlank()) icon else url,
        contentDescription = "Marker Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .padding(bottom = 10.dp)
    )
    Button(onClick = {
        if (currentRoute != null) {
            vm.modPrevScreen(currentRoute)
        }
        vm.showBottomSheet(false)
        vm.modMarcadorActual(
            lat.toDoubleOrNull() ?: 0.0,
            long.toDoubleOrNull() ?: 0.0
        )
        navigationController.navigate(Routes.CameraScreen.route)
    }) {
        Text((if (photoTaken) "Retomar foto" else "Tomar foto"))
    }


    Button(onClick = {
        if (currentRoute != null) {
            vm.modPrevScreen(currentRoute)
        }
        vm.showBottomSheet(false)
        vm.modMarcadorActual(
            lat.toDoubleOrNull() ?: 0.0,
            long.toDoubleOrNull() ?: 0.0
        )
        navigationController.navigate(Routes.GalleryScreen.route)
    }) {
        Text("Buscar foto en galería")
    }
}
