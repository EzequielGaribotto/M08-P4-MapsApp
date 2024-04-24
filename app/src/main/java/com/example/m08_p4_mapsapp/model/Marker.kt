package com.example.m08_p4_mapsapp.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.google.maps.android.compose.MarkerState

data class Marker(
    var owner: String?,
    var id: String,
    var name: String,
    var markerState: MarkerState,
    var icon: Bitmap,
    var url:String,
    var categoria: String) {

    constructor(owner:String?, id:String, name: String, markerState: MarkerState, url: String, categoria: String) : this(owner, id, name, markerState, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), url, categoria){
        this.owner = owner
        this.id = id
        this.markerState = markerState
        this.name = name
        this.url = url
        this.categoria = categoria
    }

    @JvmName("getId1")
    fun getId(): String {
        return this.id
    }

    @JvmName("getUrl1")
    fun getUri(): Uri {
        return this.url.toUri()
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

    @JvmName("getCategoria1")
    fun getCategoria(): String {
        return this.categoria
    }
    override fun toString(): String {
        return "Marker(id=$id, markerState=$markerState, name=$name, icon=$icon, url=$url)"
    }
}