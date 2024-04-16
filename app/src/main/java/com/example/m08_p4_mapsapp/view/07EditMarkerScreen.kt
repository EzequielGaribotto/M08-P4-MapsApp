package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditMarkerScreen(vm: ViewModel, navController: NavController) {
    vm.modPrevScreen("EditMarkerScreen")
    val lat by vm.inputLat.observeAsState("")
    val long by vm.inputLong.observeAsState("")
    val name by vm.markerName.observeAsState("")
    val icon by vm.icon.observeAsState()
    val url by vm.url.observeAsState("")
    val id by vm.markerId.observeAsState("")
    vm.modBottomSheet(false)

    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SetPhoto(url, icon, vm, lat, long, navController, true)
            SetName(name, vm)
            icon?.let {
                EditMarker(true, name, lat, long, vm,
                    it, url, id, navController)
            }
        }
    }
}

@Composable
private fun EditMarker(
    photoTaken: Boolean,
    name: String,
    lat: String,
    long: String,
    vm: ViewModel,
    icon: Bitmap,
    url: String,
    id: String,
    navController: NavController
) {
    val canAddMarker = photoTaken && name.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty()
    Button(onClick = {
        vm.modMarcadorActual(lat.toDouble(), long.toDouble())
        vm.modMarkerName(name)
        vm.modMarkerIcon(icon)
        vm.modUrl(url)
        vm.modInputLat(lat)
        vm.modInputLong(long)
        vm.editMarker(
            marker = Marker(
                id = id,
                markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble())),
                name = name,
                icon = icon,
                url = url

            ))

        navController.navigate(Routes.MarkerListScreen.route)
    }, enabled = canAddMarker) {
        Text("Editar marcador")
    }
}