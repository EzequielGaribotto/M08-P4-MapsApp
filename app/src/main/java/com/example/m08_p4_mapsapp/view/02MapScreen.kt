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
import com.google.maps.android.compose.rememberMarkerState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(navController: NavController, vm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        MapScreen(vm)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(vm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val prevScreen = vm.prevScreen.value
        val marcadorActual by vm.marcadorActual.observeAsState(LatLng(0.0, 0.0))
        val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(marcadorActual, 18f) }
        val getUserLocation by vm.getUserLocation.observeAsState(true)
        if (getUserLocation) {
            val context = LocalContext.current
            val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
            val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
            locationResult.addOnCompleteListener(context as MainActivity) { task ->
                if (task.isSuccessful) {
                    vm.modMarcadorActual(task.result.latitude, task.result.longitude)
                    cameraPositionState.position =  CameraPosition.fromLatLngZoom(marcadorActual, 18f)
                    vm.modGetUserLocation(false)
                } else {
                    Log.e("Error", "Exception: %s", task.exception)
                }
            }
        }
        val context = LocalContext.current
        if (prevScreen == "AddMarkerScreen") {
            vm.resetMarkerValues(context)
        }


        vm.modPrevScreen("MapScreen")

        GoogleMap(modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = {
                vm.modPhotoTaken(false)
                vm.modInputLat(it.latitude.toString())
                vm.modInputLong(it.longitude.toString())
                vm.modBottomSheet(true)
            }
        ) {
            val markers by vm.markers.observeAsState(mutableListOf())
            vm.getMarkers()
            markers?.forEach { marker ->
                val markerState = rememberMarkerState(position = marker.markerState.position)
                Marker(
                    state = markerState,
                    title = marker.name,
                    snippet = "Marker at ${markerState.position.latitude}, ${markerState.position.longitude}",
                )
            }
        }
    }
}

