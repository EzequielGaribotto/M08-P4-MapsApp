package com.example.m08_p4_mapsapp.model

import android.graphics.Bitmap
import com.google.maps.android.compose.MarkerState

class Marker {
    var owner: String?
    var id: String
    var name: String
    var markerState: MarkerState
    var icon: Bitmap
    var url:String

    constructor(owner:String?, id:String, markerState: MarkerState, name: String, icon: Bitmap, url: String) {
        this.owner = owner
        this.id = id
        this.markerState = markerState
        this.name = name
        this.icon = icon
        this.url = url
    }

    fun updId(id: String) {
        this.id = id
    }

    fun updUrl(url: String) {
        this.url = url
    }

    fun updMarkerState(markerState: MarkerState) {
        this.markerState = markerState
    }

    fun updName(name: String) {
        this.name = name
    }

    fun updIcon(icon: Bitmap) {
        this.icon = icon
    }

    @JvmName("getId1")
    fun getId(): String {
        return this.id
    }

    @JvmName("getUrl1")
    fun getUrl(): String {
        return this.url
    }

    @JvmName("getMarkerState1")
    fun getMarkerState(): MarkerState {
        return this.markerState
    }

    @JvmName("getName1")
    fun getName(): String {
        return this.name
    }

    @JvmName("getIcon1")
    fun getIcon(): Bitmap {
        return this.icon
    }

    @JvmName("getOwner1")
    fun getOwner(): String? {
        return this.owner
    }

    override fun toString(): String {
        return "Marker(id=$id, markerState=$markerState, name=$name, icon=$icon, url=$url)"
    }


}