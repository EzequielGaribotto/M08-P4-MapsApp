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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.m08_p4_mapsapp.MainActivity
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(vm: ViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val prevScreen = vm.prevScreen.value
        val posicionActual by vm.posicionActual.observeAsState(LatLng(0.0, 0.0))
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(posicionActual, 12f)
        }
        val getUserLocation by vm.getUserLocation.observeAsState(true)
        if (getUserLocation) {
            val context = LocalContext.current
            val fusedLocationProviderClient =
                remember { LocationServices.getFusedLocationProviderClient(context) }
            val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
            locationResult.addOnCompleteListener(context as MainActivity) { task ->
                if (task.isSuccessful) {
                    vm.modPosicionActual(task.result.latitude, task.result.longitude)
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(posicionActual, 12f)
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
        Map(cameraPositionState, vm)
    }
}

@Composable
private fun Map(
    cameraPositionState: CameraPositionState, vm: ViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isBuildingEnabled = true,
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            ),
            onMapLongClick = {
                vm.modInputLat(it.latitude.toString())
                vm.modInputLong(it.longitude.toString())
                vm.showBottomSheet(true)
            }) {
            val markers by vm.markers.observeAsState(emptyList())
            val categories by vm.markerCategories.observeAsState(emptyMap())
            if (markers.isEmpty()) { vm.getMarkers()
            } else {
                markers?.forEach { marker ->
                    val context = LocalContext.current
                    val markerState = rememberMarkerState(position = marker.markerState.position)
                    val iconId = categories[marker.categoria]
                    val icon = iconId?.let { id -> ContextCompat.getDrawable(context, id) }
                    val bitmapDescriptor =
                        icon?.let { BitmapDescriptorFactory.fromBitmap(it.toBitmap()) }
                    Marker(
                        state = markerState,
                        title = marker.name,
                        snippet = "Categoria: ${marker.categoria}",
                        icon = bitmapDescriptor
                    )
                }
            }
        }
    }
}


