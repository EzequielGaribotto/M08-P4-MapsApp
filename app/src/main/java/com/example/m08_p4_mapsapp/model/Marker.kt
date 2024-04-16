package com.example.m08_p4_mapsapp.model

import android.graphics.Bitmap
import com.google.maps.android.compose.MarkerState

class Marker {
    var id: String
    val markerState: MarkerState
    val name: String
    val icon: Bitmap
    var url:String

    constructor(id:String, markerState: MarkerState, name: String, icon: Bitmap, url: String) {
        this.id = id
        this.markerState = markerState
        this.name = name
        this.icon = icon
        this.url = url
    }

}