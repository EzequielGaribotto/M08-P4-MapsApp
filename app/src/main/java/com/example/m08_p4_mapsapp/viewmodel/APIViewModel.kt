package com.example.m08_p4_mapsapp.viewmodel


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.m08_p4_mapsapp.model.Marker
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState


class APIViewModel: ViewModel() {
    private val _marcadorActual = MutableLiveData(LatLng(0.0,0.0))
    val marcadorActual = _marcadorActual

    fun modMarcadorActual(lat: Double, long: Double) {
        _marcadorActual.value = LatLng(lat, long)
    }

    private val _showBottomSheet =  MutableLiveData(false)
    val showBottomSheet = _showBottomSheet
    private val _markers = MutableLiveData<MutableList<Marker>>()
    val markers = _markers

    fun addMarker(lat: String, long: String, name: String, icon: Bitmap) {
        val markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble()))
        val markersTemp = _markers.value ?: mutableListOf()
        markersTemp.add(Marker(markerState, name, icon))
        _markers.value = markersTemp
    }

    fun updateMarkerIcon(icon: Bitmap) {
        _icon.value = icon
    }
    fun switchBottomSheet(boolean: Boolean) {
        _showBottomSheet.value = boolean
    }

    private val _inputLat = MutableLiveData("")
    val inputLat = _inputLat
    private val _inputLong = MutableLiveData("")
    val inputLong = _inputLong
    private val _markerName = MutableLiveData("")
    val markerName = _markerName
    private val _icon = MutableLiveData<Bitmap>()
    val icon = _icon
    fun modMarkerName(name: String) {
        _markerName.value = name
    }

    fun modInputLat(lat: String) {
        _inputLat.value = lat
    }

    fun modInputLong(long: String) {
        _inputLong.value = long
    }
}
