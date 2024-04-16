package com.example.m08_p4_mapsapp.viewmodel


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.firebase.Repository
import com.example.m08_p4_mapsapp.model.Marker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.MarkerState
import com.example.m08_p4_mapsapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.UUID

class APIViewModel : ViewModel() {

    val _showErrorMessage = MutableLiveData(false)
    val showErrorMessage = _showErrorMessage



    // Funcion que determina si dos bitmaps son iguales o no
    val _userRegister = MutableLiveData(false)
    val userRegister = _userRegister

    val _userLogin = MutableLiveData(false)
    val userLogin = _userLogin



    val _email = MutableLiveData("")
    val email = _email

    val _password = MutableLiveData("")
    val password = _password

    fun modPassword(password: String) {
        _password.value = password
    }
    fun modEmail(email: String) {
        _email.value = email
    }
    fun modUserRegister(boolean: Boolean) {
        _userRegister.value = boolean
    }

    fun modShowErrorMessage(boolean: Boolean) {
        _showErrorMessage.value = boolean
    }
    fun modUserLogin(boolean: Boolean) {
        _userLogin.value = boolean
    }

    private val _url = MutableLiveData("")
    val url = _url

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    private val repo = Repository()


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

    fun getMarkers() {
        repo.getMarkersFromDatabase().addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }
                val tempList = mutableListOf<Marker>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val document = dc.document
                        val id = document.getString("id") ?: ""
                        val latitude = document.get("latitude") ?: ""
                        val longitude = document.get("longitude") ?: ""
                        val name = document.getString("name") ?: ""
                        val url = document.getString("url") ?: ""

                        val newMark = Marker(
                            id,
                            MarkerState(
                                LatLng(
                                    latitude.toString().toDouble(), longitude.toString().toDouble()
                                )
                            ), name, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), url
                        )
                        tempList.add(newMark)
                    }
                }
                _markers.value = tempList
            }
        })
    }
//    fun getMarkers() {
//        repo.getMarkersFromDatabase().addSnapshotListener { value, error ->
//            if (error != null) {
//                Log.e("Firestore error", error.message.toString())
//                return@addSnapshotListener
//            }
//            val tempList = mutableListOf<Marker>()
//            for (dc: DocumentChange in value?.documentChanges!!) {
//                if (dc.type == DocumentChange.Type.ADDED) {
//                    val newMarker = dc.document.toObject(Marker::class.java)
//                    newMarker.id = dc.document.id
//                    tempList.add(newMarker)
//                }
//            }
//            _markers.value = tempList
//        }
//    }

//    fun getMarkers() {
//        repo.getMarkersFromDatabase().addSnapshotListener { value, error ->
//            if (error != null) {
//                Log.e("Firestore error", error.message.toString())
//                return@addSnapshotListener
//            }
//            val tempList = mutableListOf<Marker>()
//            for (dc: DocumentChange in value?.documentChanges!!) {
//                if (dc.type == DocumentChange.Type.ADDED) {
//                    val newMarker = dc.document.toObject(Marker::class.java)
//                    newMarker.id = dc.document.id
//                    tempList.add(newMarker)
//                }
//            }
//            _markers.value = tempList
//        }
//    }

    fun removeMarker(marker: Marker) {
        repo.removeMarker(marker)
    }

    fun modUrl(url: String) {
        _url.value = url
    }

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
        if (isValidEmail(username)) {
            auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                    Log.d("ERROR", "User registered")
                } else {
                    _goToNext.value = false
                    Log.d("ERROR", "Error creating user : ${task.result}")
                }
                modifyProcessing()
            }
        } else {
            _goToNext.value = false
            Log.d("ERROR", "Invalid email")
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
            .matches()
    }

    fun isValidPassword(password: CharSequence?): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return password != null && passwordMatcher.matches(password)
    }

    fun login(username: String?, password: String?) {
        if (isValidEmail(username)) {
            auth.signInWithEmailAndPassword(username!!, password!!).addOnCompleteListener { task ->
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
        } else {
            _goToNext.value = false
            Log.d("Error", "Invalid email")
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

    fun uploadImage(imageUri: Uri) {
        repo.uploadImage(imageUri, _markers.value?.last()!!)
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

    fun addMarker(lat: String, long: String, name: String, icon: Bitmap, url: String) {
        val markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble()))
        val markersTemp = _markers.value?.toMutableSet() ?: mutableSetOf()
        val id = UUID.randomUUID().toString()
        markersTemp.add(Marker(id,markerState, name, icon, url))
        _markers.value = markersTemp.toMutableList()
        repo.addMarker(_markers.value?.last()!!)
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

    fun resetMarkerValues(context: Context) {
        _inputLat.value = ""
        _inputLong.value = ""
        _markerName.value = ""
        val img: Bitmap =
            ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
        _icon.value = img
        _photoTaken.value = false
        _url.value = ""
    }

    fun modPrevScreen(screen: String) {
        _prevScreen.value = screen
    }

    fun goBack(
        userLogin: Boolean, userRegister: Boolean, navController: NavController, prevScreen: String
    ) {
        if (userLogin || userRegister) {
            modUserLogin(false)
            modUserRegister(false)
            modShowErrorMessage(false)
        } else {
            navController.navigate(prevScreen)
        }
    }
}
