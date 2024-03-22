package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.MainActivity
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(navController: NavController, avm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        MapScreen(avm)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(avm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        avm.modPrevScreen("MapScreen")
        val marcadorActual by avm.marcadorActual.observeAsState(LatLng(0.0, 0.0))
        val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(marcadorActual, 18f) }
        val getUserLocation by avm.getUserLocation.observeAsState(true)
        if (getUserLocation) {
            val context = LocalContext.current
            val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
            val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
            locationResult.addOnCompleteListener(context as MainActivity) { task ->
                if (task.isSuccessful) {
                    avm.modMarcadorActual(task.result.latitude, task.result.longitude)
                    cameraPositionState.position =  CameraPosition.fromLatLngZoom(marcadorActual, 18f)
                    avm.modGetUserLocation(false)
                } else {
                    Log.e("Error", "Exception: %s", task.exception)
                }
            }
        }
        GoogleMap(modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = {
                avm.switchPhotoTaken(false)
                avm.modInputLat(it.latitude.toString())
                avm.modInputLong(it.longitude.toString())
                avm.switchBottomSheet(true)
            }
        ) {
            val markers by avm.markers.observeAsState(mutableListOf())
            markers?.forEach {
                Marker(
                    state = it.markerState,
                    title = it.name,
                    snippet = "Marker at ${it.markerState.position.latitude}, ${it.markerState.position.longitude}",
                )
            }
        }
    }
}

