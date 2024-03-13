package com.example.m08_p4_mapsapp.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState


class APIViewModel: ViewModel() {
    private val _marcadorActual = MutableLiveData(LatLng(41.4534265, 2.1837151))
    val marcadorActual = _marcadorActual

    fun modMarcadorActual(lat: Double, long: Double) {
        _marcadorActual.value = LatLng(lat, long)
    }

    private val _showBottomSheet =  MutableLiveData(false)
    val showBottomSheet = _showBottomSheet
    val markers = MutableLiveData<Map<String,MarkerState>?>(mutableMapOf())

    fun addMarker(lat: String, long: String, name: String) {
        val newMarker = MarkerState(LatLng(lat.toDouble(), long.toDouble()))
        val updatedMarkers = markers.value?.toMutableMap()
        updatedMarkers?.put(name,newMarker)
        markers.value = updatedMarkers
    }
    fun switchBottomSheet(boolean: Boolean) {
        _showBottomSheet.value = boolean
    }

    private val _selectedFile = MutableLiveData("")
    val selectedFile = _selectedFile

    fun modSelectedFile(file:String) {
        _selectedFile.value = file
    }

    private val _expandedFile = MutableLiveData(false)
    val expandedFile = _expandedFile

    fun switchExpandFile(boolean: Boolean) {
        _expandedFile.value = boolean
    }

    private val _inputLat = MutableLiveData("")
    val inputLat = _inputLat
    private val _inputLong = MutableLiveData("")
    val inputLong = _inputLong
    private val _markerName = MutableLiveData("")
    val markerName = _markerName
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
