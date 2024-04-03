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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import java.nio.ByteBuffer

class APIViewModel : ViewModel() {

    // Funcion que determina si dos bitmaps son iguales o no

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    private val repo = repository()


    private val _usersList = MutableLiveData<List<User>>()
    val usersList = _usersList

    private val _goToNext = MutableLiveData(false)
    val goToNext = _goToNext

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar = _showProgressBar

    private val _userId = MutableLiveData<String>()
    val userId = _userId

    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser

    private val _actualUser = MutableLiveData<User?>()
    val actualUser = _actualUser

    private val _userName = MutableLiveData("")
    val userName = _userName

    private val _age = MutableLiveData("")
    val age = _age

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


    fun getUsers() {
        repo.getUsers().addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<User>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val newUser = dc.document.toObject(User::class.java)
                    newUser.userId = dc.document.id
                    tempList.add(newUser)
                }
            }
            _usersList.value = tempList
        }
    }

    fun register(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                    Log.d("ERROR", "User registered")
                } else {
                    _goToNext.value = false
                    Log.d("ERROR", "Error creating user : ${task.result}")
                }
                modifyProcessing()
            }
    }
    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error signing in: ${task.result}")
                }
                modifyProcessing()
            }
    }

    fun modifyProcessing() {
        _showProgressBar.value = (showProgressBar.value == true)
    }
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
