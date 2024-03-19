package com.example.m08_p4_mapsapp.viewmodel


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.m08_p4_mapsapp.model.Marker
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState


class APIViewModel: ViewModel() {
    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    fun setCameraPermissionGranted(granted:Boolean) {
        _cameraPermissionGranted.value = granted

    }

    fun setShouldShowPermissionRationale(should:Boolean) {
        _shouldShowPermissionRationale.value = should
    }


    fun setShowPermissionDenied(denied:Boolean) {
        _showPermissionDenied.value = denied
    }
    private val _marcadorActual = MutableLiveData(LatLng(0.0,0.0))
    val marcadorActual = _marcadorActual

    private val _showBottomSheet =  MutableLiveData(false)
    val showBottomSheet = _showBottomSheet

    private val _markers = MutableLiveData<MutableList<Marker>>()
    val markers = _markers

    private val _inputLat = MutableLiveData("")
    val inputLat = _inputLat

    private val _inputLong = MutableLiveData("")
    val inputLong = _inputLong

    private val _markerName = MutableLiveData("")
    val markerName = _markerName

    private val _icon = MutableLiveData<Bitmap>()
    val icon = _icon

    private val _photoTaken = MutableLiveData<Boolean>()
    val photoTaken = _photoTaken

    fun switchPhotoTaken(boolean: Boolean) {
        _photoTaken.value = boolean
    }

    fun switchBottomSheet(boolean: Boolean) {
        _showBottomSheet.value = boolean
    }
    fun modMarcadorActual(lat: Double, long: Double) {
        _marcadorActual.value = LatLng(lat, long)
    }

    fun addMarker(lat: String, long: String, name: String, icon: Bitmap) {
        val markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble()))
        val markersTemp = _markers.value?.toMutableSet() ?: mutableSetOf()
        markersTemp.add(Marker(markerState, name, icon))
        _markers.value = markersTemp.toMutableList()
    }

    fun updateMarkerIcon(icon: Bitmap) {
        _icon.value = icon
    }

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
