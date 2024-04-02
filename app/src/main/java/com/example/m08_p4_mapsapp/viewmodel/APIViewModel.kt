package com.example.m08_p4_mapsapp.viewmodel


import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.m08_p4_mapsapp.firebase.repository
import com.example.m08_p4_mapsapp.model.Marker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.MarkerState
import com.example.m08_p4_mapsapp.model.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference

class APIViewModel : ViewModel() {
    private val database = FirebaseFirestore.getInstance()

    private val repo = repository()
    private val _usersList = MutableLiveData<List<User>>()
    val usersList = _usersList
    fun getUsers() {
        repo.getUsers().addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<User>()
            for (dc:DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val newUser = dc.document.toObject(User::class.java)
                    newUser.userId = dc.document.id
                    tempList.add(newUser)
                }
            }
            _usersList.value = tempList
        }
    }

    val _actualUser = MutableLiveData<User?>()
    val actualUser = _actualUser
    val _userName = MutableLiveData("")
    val userName = _userName
    val _age = MutableLiveData("")

    fun getUser(userId: String) {
        repo.getUser(userId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w("UserRepository", "Listen failed.", error)
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val user = value.toObject(User::class.java)
                if (user != null) {
                    user.userId = userId
                }

                _actualUser.value = user
                _userName.value = _actualUser.value!!.userName
                _age.value = _actualUser.value!!.age.toString()

            } else {
                Log.d("UserRepository", "Current data: null")
            }
        }
    }




    private val _prevScreen = MutableLiveData("MapScreen")
    val prevScreen = _prevScreen

    private val _getUserLocation = MutableLiveData(true)
    val getUserLocation = _getUserLocation

    private val _marcadorActual = MutableLiveData(LatLng(0.0, 0.0))
    val marcadorActual = _marcadorActual

    private val _showBottomSheet = MutableLiveData(false)
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

    fun modGetUserLocation(boolean: Boolean) {
        _getUserLocation.value = boolean
    }

    fun resetMarkerValues() {
        _inputLat.value = ""
        _inputLong.value = ""
        _markerName.value = ""
        _icon.value = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        _photoTaken.value = false
    }

    fun modPrevScreen(screen: String) {
        _prevScreen.value = screen
    }
}
