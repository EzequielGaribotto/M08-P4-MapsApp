package com.example.m08_p4_mapsapp.model

import android.graphics.Bitmap
import com.google.maps.android.compose.MarkerState

data class Marker (
    val markerState: MarkerState,
    val name: String,
    val icon: Bitmap
)