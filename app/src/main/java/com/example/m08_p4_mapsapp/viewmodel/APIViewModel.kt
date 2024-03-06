package com.example.m08_p4_mapsapp.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class APIViewModel: ViewModel() {
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

    fun modInputLat(lat: String) {
        _inputLat.value = lat
    }

    fun modInputLong(long: String) {
        _inputLong.value = long
    }
}
