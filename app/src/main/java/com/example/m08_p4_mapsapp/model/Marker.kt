package com.example.m08_p4_mapsapp.model

import android.graphics.Bitmap
import com.google.maps.android.compose.MarkerState

class Marker {
    val markerState: MarkerState
    val name: String
    val icon: Bitmap
    val url:String

    constructor(markerState: MarkerState, name: String, icon: Bitmap, url: String) {
        this.markerState = markerState
        this.name = name
        this.icon = icon
        this.url = url
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Marker) {
            this.markerState == other.markerState
        } else {
            false
        }
    }
}