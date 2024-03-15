package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
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
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel


@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MarkerListScreen(navController: NavController,  avm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val markers by avm.markers.observeAsState(listOf<Marker>())
        LazyColumn {
            items(markers) { image ->
                MarkerItem(marker = image) { lat, long ->

                    avm.modMarcadorActual(lat, long)
                    navController.navigate(Routes.MapScreen.route)
                }

            }
        }
        markers?.forEach {

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MarkerItem(marker: Marker, onClickGo: (Double, Double) -> Unit) {
    Card {
        val lat = marker.markerState.position.latitude
        val long = marker.markerState.position.longitude
        val photo = marker.icon
        val name = marker.name

        Row {
            GlideImage(
                model = photo,
                contentDescription = "Character Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )
        }

        Text(name)
        Text(text = lat.toString())
        Text(text = long.toString())
        Button(onClick = {
            onClickGo(lat,long)
        }) {
            Text("GO!")
        }
    }
}
