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

    override fun equals(other: Any?): Boolean {
        return if (other is Marker) {
            this.id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + markerState.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }


}