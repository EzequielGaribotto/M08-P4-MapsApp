package com.example.m08_p4_mapsapp.viewmodel


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.firebase.Repository
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.MarkerState
import com.example.m08_p4_mapsapp.model.UserPrefs
import com.example.m08_p4_mapsapp.navigation.Routes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class ViewModel : ViewModel() {


    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    private val repo = Repository()

    private val _url = MutableLiveData("")
    val url = _url

    private val _goToNext = MutableLiveData(false)
    val goToNext = _goToNext

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

    private val _selectedImage = MutableLiveData<Bitmap>()
    val selectedImage = _selectedImage

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> = _email

    private val _nombre = MutableLiveData<String>("")
    val nombre: LiveData<String> = _nombre

    private val _apellido = MutableLiveData<String>("")
    val apellido: LiveData<String> = _apellido

    private val _ciudad = MutableLiveData<String>("")
    val ciudad: LiveData<String> = _ciudad

    private val _password = MutableLiveData<String>("")
    val password: LiveData<String> = _password

    private val _errorPass = MutableLiveData<Boolean>()
    val errorPass: LiveData<Boolean> = _errorPass

    private val _showLoginDialog = MutableLiveData<Boolean>()
    val showLoginDialog: LiveData<Boolean> = _showLoginDialog

    private val _isLoading = MutableLiveData(true)
    val isLoading = _isLoading

    private val _validLogin = MutableLiveData<Boolean>()
    val validLogin: LiveData<Boolean> = _validLogin

    private val _userId = MutableLiveData<String>()

    private val _verContrasena = MutableLiveData<Boolean>()
    val verContrasena = _verContrasena

    private val _permanecerLogged = MutableLiveData<Boolean>()
    val keepLogged = _permanecerLogged

    private val _markerId = MutableLiveData("")
    val markerId = _markerId

    private val _selectedUri = MutableLiveData<Uri>()
    val selectedUri = _selectedUri

    private val _showRegisterDialog = MutableLiveData<Boolean>()
    val showRegisterDialog: LiveData<Boolean> = _showRegisterDialog

    private val _errorEmail = MutableLiveData<Boolean>()
    val errorEmail: LiveData<Boolean> = _errorEmail

    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser

    // live data para solicitarregistrar

    private val _showRegisterRequestDialog = MutableLiveData(false)
    val showRegisterRequestDialog = _showRegisterRequestDialog

    fun showRegisterRequestDialog(value: Boolean) {
        _showRegisterRequestDialog.value = value
    }

    fun removeMarker(marker: Marker) {
        repo.removeMarker(marker)
    }

    fun modUrl(url: String) {
        _url.value = url
    }

    fun modificarEmailState(value: String) {
        _email.value = value
    }

    fun modificarPasswordState(value: String) {
        _password.value = value
    }

    fun modificarNombreState(value: String) {
        _nombre.value = value
    }

    fun modificarApellidoState(value: String) {
        _apellido.value = value
    }

    fun modificarCiudadState(value: String) {
        _ciudad.value = value
    }

    fun showLoginDialog(value: Boolean) {
        _showLoginDialog.value = value
    }

    fun modInvalidPass(value: Boolean) {
        _errorPass.value = value
    }

    fun modInvalidEmail(value: Boolean) {
        _errorEmail.value = value
    }

    fun modifyProcessing(newValue: Boolean) {
        _isLoading.value = newValue
    }

    fun showRegisterDialog(value: Boolean) {
        _showRegisterDialog.value = value
    }

    fun getLoggedUser(): String {
        return _loggedUser.value.toString()
    }

    fun modPrevScreen(screen: String) {
        _prevScreen.value = screen
    }

    fun goBack(navController: NavController, prevScreen: String) {
        navController.navigate(prevScreen)
    }

    fun editMarker(marker: Marker) {
        repo.editMarker(marker)
    }

    fun modMarkerId(id: String) {
        _markerId.value = id
    }

    fun modSelectedImage(selectedBitmap: Bitmap) {
        _selectedImage.value = selectedBitmap
    }

    fun modSelectedUri(uri: Uri) {
        _selectedUri.value = uri
    }

    fun modificarLoggedUser(nuevo: String) {
        _loggedUser.value = nuevo
    }

    fun modVerContrasena(nuevoBoolean: Boolean) {
        _verContrasena.value = nuevoBoolean
    }

    fun modKeepLogged(nuevoBoolean: Boolean) {
        _permanecerLogged.value = nuevoBoolean
    }

    fun showBottomSheet(boolean: Boolean) {
        _showBottomSheet.value = boolean
    }

    fun modMarcadorActual(lat: Double, long: Double) {
        _marcadorActual.value = LatLng(lat, long)
    }

    fun modMarkerIcon(icon: Bitmap) {
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

    fun getMarkers() {
        repo.getMarkersFromDatabase()
            .whereEqualTo("owner", _loggedUser.value)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore error", error.message.toString())
                        return
                    }
                    val tempList = mutableListOf<Marker>()
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val document = dc.document
                            val owner = document.getString("owner") ?: ""
                            val id = document.getString("id") ?: ""
                            val latitude = document.get("latitude") ?: ""
                            val longitude = document.get("longitude") ?: ""
                            val name = document.getString("name") ?: ""
                            val url = document.getString("url") ?: ""

                            val newMark = Marker(
                                owner,
                                id,
                                MarkerState(
                                    LatLng(
                                        latitude.toString().toDouble(),
                                        longitude.toString().toDouble()
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

    fun register(context: Context, username: String, password: String) {
        val userPrefs = UserPrefs(context)
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                    modifyProcessing(false)
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(_email.value!!, _password.value!!)
                    }
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                repo.addUser(
                                    User(_nombre.value!!,
                                        _apellido.value!!,
                                        _ciudad.value!!,
                                        _loggedUser.value!!)
                                )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user : ${task.exception}")
                    modifyProcessing(true)
                }
            }
    }


    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && target?.let {
            android.util.Patterns.EMAIL_ADDRESS.matcher(it)
                .matches()
        } == true
    }

    fun isValidPass(password: CharSequence?): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return password != null && passwordMatcher.matches(password)
    }

    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    _goToNext.value = true
                    modifyProcessing(false)
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                repo.addUser(
                                    User(_nombre.value!!, _apellido.value!!,
                                        _ciudad.value!!, _loggedUser.value!!)
                                )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error logging in: ${task.exception}")
                    modifyProcessing(true)
                    _showRegisterRequestDialog.value = true
                }
            }
            .addOnFailureListener {
                _validLogin.value = false
            }
    }

    fun signOut(context: Context, navController: NavController) {

        val userPrefs = UserPrefs(context)

        CoroutineScope(Dispatchers.IO).launch {
            userPrefs.clearUserData()
        }

        auth.signOut()

        _goToNext.value = false
        _password.value = ""

        modifyProcessing(true)

        resetUserValues()
        resetMarkerValues(context)

        navController.navigate(Routes.LoginScreen.route)
    }

    private fun resetUserValues() {
        _loggedUser.value = ""
        _userId.value = ""
        _nombre.value = ""
        _apellido.value = ""
        _ciudad.value = ""
        _showRegisterRequestDialog.value = false
        _showRegisterDialog.value = false
        _showLoginDialog.value = false
        _validLogin.value = false
        _verContrasena.value = false
        _permanecerLogged.value = false
        _markers.value = mutableListOf()
        _inputLat.value = ""
        _inputLong.value = ""
        _markerName.value = ""
        _icon.value = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        _url.value = ""
        _showBottomSheet.value = false
        _marcadorActual.value = LatLng(0.0, 0.0)
        _getUserLocation.value = true
        _prevScreen.value = "MapScreen"
        _errorPass.value = false
        _errorEmail.value = false
        _markerId.value = ""
        _selectedImage.value = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        _selectedUri.value = Uri.EMPTY
    }

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            modifyProcessing(false)
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("GOOGLE_SIGNIN", "Log con exito")
                            val userRef =
                                database.collection("user").whereEqualTo("owner", _loggedUser.value)
                            userRef.get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {
                                        database.collection("user")
                                            .add(
                                                hashMapOf(
                                                    "owner" to _loggedUser.value,
                                                    "name" to _nombre.value,
                                                    "apellido" to _apellido.value,
                                                    "ciudad" to _ciudad.value,
                                                )
                                            )
                                    }
                                }
                            home()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("GOOGLE_SIGNIN", "Fallo .addOnFailureListener")
                    }
            } catch (ex: Exception) {
                Log.d("GOOGLE_SIGNIN", "Excepción" + ex.localizedMessage)
            }
        }


    fun addMarker(lat: String, long: String, name: String, icon: Bitmap, url: String) {
        val markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble()))
        val markersTemp = _markers.value?.toMutableSet() ?: mutableSetOf()
        val id = UUID.randomUUID().toString()
        markersTemp.add(Marker(_loggedUser.value, id, markerState, name, icon, url))
        _markers.value = markersTemp.toMutableList()
        repo.addMarker(_markers.value?.last()!!)
    }

    fun uploadImage(imageUri: Uri) {
        repo.uploadImage(imageUri, _markers.value?.last()!!)
    }

    fun resetMarkerValues(context: Context) {
        _inputLat.value = ""
        _inputLong.value = ""
        _markerName.value = ""
        val img: Bitmap =
            ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
        _icon.value = img
        _url.value = ""
    }

    private val _successfulRegister = MutableLiveData<Boolean>()
    val successfulRegister: LiveData<Boolean> = _successfulRegister
    fun showSuccessfulRegisterDialog(b: Boolean) {
        _successfulRegister.value = b
    }
}
