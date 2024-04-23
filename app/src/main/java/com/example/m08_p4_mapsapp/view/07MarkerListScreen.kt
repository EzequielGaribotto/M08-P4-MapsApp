package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.m08_p4_mapsapp.CustomDialog
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.ui.theme.DarkBrown
import com.example.m08_p4_mapsapp.ui.theme.LightBrown
import com.example.m08_p4_mapsapp.viewmodel.ViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MarkerListScreen(navController: NavController,  vm: ViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val markers:MutableList<Marker> by vm.markers.observeAsState(mutableListOf())
        vm.getMarkers()
        val deleteMarkerDialog by vm.showDeleteMarkerDialog.observeAsState(false)
        val marker by vm.currentMarker.observeAsState()
        CustomDialog(
            show = deleteMarkerDialog,
            question = "¿Estás seguro de que quieres eliminar el marcador?",
            option1 = "SI",
            onOption1Click = {
                vm.showDeleteMarkerDialog(false)
                vm.removeMarker(marker!!)
                             },
            option2 = "NO",
            onOption2Click = { vm.showDeleteMarkerDialog(false) }
        )
        if (markers.isNotEmpty()) {
            LazyColumn {
                items(markers) { marker ->
                    vm.modCurrentMarker(marker)
                    MarkerItem(marker, vm, navController) { lat, long ->
                        vm.modPosicionActual(lat, long)
                        navController.navigate(Routes.MapScreen.route)
                    }
                }
            }
        } else {
            Text("Has d'afegir primer marcadors")
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MarkerItem(marker: Marker, vm: ViewModel, navController: NavController, onClickGo: (Double, Double) -> Unit) {
    val lat = marker.getMarkerState().position.latitude
    val long = marker.getMarkerState().position.longitude
    val photo = marker.getIcon()
    val name = marker.getName()
    val uri = marker.getUri()
    val id = marker.getId()
    val context = LocalContext.current
    val inputStream = remember(marker.getUri()) {
        try {
            context.contentResolver.openInputStream(marker.getUri())
        } catch (e: Exception) {
            // Log the exception
            Log.e("MarkerItem", "Failed to open InputStream", e)
            null
        }
    }

    val painter = if (inputStream != null) {
        rememberImagePainter(
            request = ImageRequest.Builder(context)
                .data(inputStream)
                .build()
        )
    } else {
        // Provide a default painter or handle the error case
        null
    }

    Card(
        border = BorderStroke(2.dp, DarkBrown),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Box(modifier = Modifier
            .background(LightBrown)
            .clickable {
                onClickGo(lat, long)
            }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                println(uri)
                if (painter != null) {
                    Image(
                        painter = painter,
                        contentDescription = "Marker Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Text("Lat. $long")
                    Text("Long. $long")
                }
            }
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Eliminar marcador",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        vm.showDeleteMarkerDialog(true)
                    }
            )
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar marcador",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        vm.modMarkerName(name)
                        vm.modMarkerIcon(photo)
                        vm.modUrl(uri)
                        vm.modInputLat(lat.toString())
                        vm.modInputLong(long.toString())
                        vm.modMarkerId(id)
                        vm.modPrevScreen("MarkerListScreen")
                        vm.showBottomSheet(false)
                        navController.navigate(Routes.EditMarkerScreen.route)
                    }
            )
        }
    }
}
