package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel
import com.google.maps.android.compose.MarkerState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MarkerListScreen(avm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val markers by avm.markers.observeAsState(mutableMapOf())
        markers?.forEach {
            Card {
                Text(it.key)
                Text(text = it.value.position.latitude.toString())
                Text(text = it.value.position.longitude.toString())
                Button(onClick = { /*TODO*/ }) {
                    Text("GO!")
                }
            }
        }
    }
}