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

    private val _url = MutableLiveData(Uri.EMPTY)
    val url = _url

    private val _goToNext = MutableLiveData(false)
    val goToNext = _goToNext

    private val _prevScreen = MutableLiveData("MapScreen")
    val prevScreen = _prevScreen

    private val _getUserLocation = MutableLiveData(true)
    val getUserLocation = _getUserLocation

    private val _posicionActual = MutableLiveData(LatLng(0.0, 0.0))
    val posicionActual = _posicionActual

    private val _showBottomSheet = MutableLiveData(false)
    val showBottomSheet = _showBottomSheet

    private val _markers = MutableLiveData(mutableListOf<Marker>())
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

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _nombre = MutableLiveData("")
    val nombre: LiveData<String> = _nombre

    private val _apellido = MutableLiveData("")
    val apellido: LiveData<String> = _apellido

    private val _ciudad = MutableLiveData("")
    val ciudad: LiveData<String> = _ciudad

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _errorPass = MutableLiveData(false)
    val errorPass: LiveData<Boolean> = _errorPass

    private val _showLoginDialog = MutableLiveData(false)
    val showLoginDialog: LiveData<Boolean> = _showLoginDialog

    private val _isLoading = MutableLiveData(true)
    val isLoading = _isLoading

    private val _userId = MutableLiveData("")

    private val _currentUser = MutableLiveData<User?>()
    val currentUser = _currentUser

    private val _verContrasena = MutableLiveData(false)
    val verContrasena = _verContrasena

    private val _keepLogged = MutableLiveData(false)
    val keepLogged = _keepLogged

    private val _markerId = MutableLiveData("")
    val markerId = _markerId

    private val _selectedUri = MutableLiveData<Uri>()
    val selectedUri = _selectedUri

    private val _showRegisterDialog = MutableLiveData(false)
    val showRegisterDialog: LiveData<Boolean> = _showRegisterDialog

    private val _errorEmail = MutableLiveData(false)
    val errorEmail: LiveData<Boolean> = _errorEmail

    private val _loggedUser = MutableLiveData("")
    val loggedUser = _loggedUser

    private val _showRegisterRequestDialog = MutableLiveData(false)
    val showRegisterRequestDialog = _showRegisterRequestDialog

    private val _successfulRegister = MutableLiveData<Boolean>()
    val successfulRegister: LiveData<Boolean> = _successfulRegister

    private val _showLogOutDialog = MutableLiveData<Boolean>()
    val showLogOutDialog: LiveData<Boolean> = _showLogOutDialog

    private val _showSaveChangesDialog = MutableLiveData(false)
    val showSaveChangesDialog: LiveData<Boolean> = _showSaveChangesDialog

    private val _showDeleteMarkerDialog = MutableLiveData(false)
    val showDeleteMarkerDialog: LiveData<Boolean> = _showDeleteMarkerDialog

    private val _currentMarker: MutableLiveData<Marker?> = MutableLiveData()

    private val _showFilter = MutableLiveData(false)
    val showFilter = _showFilter

    private val _nameFilter = MutableLiveData("")
    val nameFilter = _nameFilter

    private val _categoryFilter = MutableLiveData("")
    val categoryFilter = _categoryFilter

    private val _markerCategories = MutableLiveData(mutableMapOf<String,Int>())
    val markerCategories = _markerCategories

    private val _deletingMarker: MutableLiveData<Marker?> = MutableLiveData()
    val deletingMarker = _deletingMarker

    private val _showSaveUserChangesDialog = MutableLiveData(false)
    val showSaveUserChangesDialog = _showSaveUserChangesDialog

    private val _showDeleteUserDialog = MutableLiveData(false)
    val showDeleteUserDialog = _showDeleteUserDialog

    private val _category = MutableLiveData("")
    val category = _category

    private val _selectedUserPfp = MutableLiveData<Bitmap>()
    val selectedPfp = _selectedUserPfp

    private val _selectedPfpUri = MutableLiveData(Uri.EMPTY)
    val selectedPfpUri = _selectedPfpUri

    fun modPfpUri(uri: Uri) {
        _selectedPfpUri.value = uri
    }

    fun modSelectedPfp(bitmap: Bitmap) {
        _selectedUserPfp.value = bitmap
    }

    fun switchShowFilter() {
        _showFilter.value = !_showFilter.value!!
    }

    fun modNameFilter(name: String) {
        _nameFilter.value = name
    }

    fun modCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun modCategory(category: String) {
        _category.value = category
    }

    fun modDeletingMarker(marker: Marker) {
        _deletingMarker.value = marker
    }

    fun showDeleteUserDialog(b: Boolean) {
        _showDeleteUserDialog.value = b
    }
    fun showSaveUserChangesDialog(b: Boolean) {
        _showSaveUserChangesDialog.value = b
    }

    fun showDeleteMarkerDialog(boolean: Boolean) {
        _showDeleteMarkerDialog.value = boolean
    }

    fun showSaveChangesDialog(b: Boolean) {
        _showSaveChangesDialog.value = b
    }

    fun showSuccessfulRegisterDialog(b: Boolean) {
        _successfulRegister.value = b
    }

    fun showLogOutDialog(b: Boolean) {
        _showLogOutDialog.value = b
    }

    fun modGoToNext(b: Boolean) {
        _goToNext.value = b
    }

    fun showRegisterRequestDialog(boolean: Boolean) {
        _showRegisterRequestDialog.value = boolean
    }

    fun removeMarker(marker: Marker) {
        repo.removeMarker(marker)
    }

    fun modUrl(url: Uri) {
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

    fun showLoginDialog(boolean: Boolean) {
        _showLoginDialog.value = boolean
    }

    fun modErrorPass(boolean: Boolean) {
        _errorPass.value = boolean
    }

    fun modErrorEmail(boolean: Boolean) {
        _errorEmail.value = boolean
    }

    fun modShowLoading(boolean: Boolean) {
        _isLoading.value = boolean
    }

    fun showRegisterDialog(boolean: Boolean) {
        _showRegisterDialog.value = boolean
    }

    fun modPrevScreen(screen: String) {
        _prevScreen.value = screen
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

    fun modVerContrasena(boolean: Boolean) {
        _verContrasena.value = boolean
    }

    fun modKeepLogged(boolean: Boolean) {
        _keepLogged.value = boolean
    }

    fun showBottomSheet(boolean: Boolean) {
        _showBottomSheet.value = boolean
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

    fun modPosicionActual(lat: Double, long: Double) {
        _posicionActual.value = LatLng(lat, long)
    }

    fun goBack(navController: NavController, prevScreen: String) {
        navController.navigate(prevScreen)
    }

    fun editMarker(marker: Marker) {
        repo.editMarker(marker)
    }

    fun getMarkerCategories() {
        for (r in R.drawable::class.java.declaredFields) {
            if (r.name.startsWith("cat_")) {
                _markerCategories.value?.put(r.name.replace("cat_",""),r.getInt(r))
            }
        }
    }

    fun getUser() {
        repo.getUsers().whereEqualTo("owner", _loggedUser.value).get().addOnSuccessListener { documents ->
            for (document in documents) {
                _currentUser.value = User(
                    document.getString("avatarUrl") ?: "",
                    document.getString("nombre") ?: "",
                    document.getString("apellido") ?: "",
                    document.getString("ciudad") ?: "",
                    document.getString("owner") ?: ""
                )
                println("User found: ${_currentUser.value}")
            }
        }.addOnFailureListener {
            Log.d("ERROR", "Error getting documents: ", it)
        }
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
                            val markerState = MarkerState(
                                LatLng(
                                    latitude.toString().toDouble(),
                                    longitude.toString().toDouble()
                                )
                            )

                            val name = document.getString("name") ?: ""
                            val url = document.getString("url") ?: ""
                            val categoria = document.getString("categoria") ?: ""

                            val newMark = Marker(owner, id, name, markerState, url, categoria)
                            tempList.add(newMark)
                        }
                    }
                    _markers.value = tempList
                }
            })
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

    fun register(
        username: String,
        password: String,
        keepLogged: Boolean = false,
        userPrefs: UserPrefs
    ) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    _goToNext.value = true
                    modShowLoading(false)
                    if (keepLogged) {
                        CoroutineScope(Dispatchers.IO).launch {
                            userPrefs.saveUserData(_email.value!!, _password.value!!)
                        }
                    }
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                repo.addUser(
                                    User(_avatarUrl.value?:"",
                                        _nombre.value!!, _apellido.value!!,
                                        _ciudad.value!!, _email.value!!
                                    )
                                )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user : ${task.exception}")
                    modShowLoading(true)
                }
            }
    }

    fun updateNombre(newNombre: String) {
        _currentUser.value?.nombre = newNombre
    }

    fun updateApellido(newApellido: String) {
        _currentUser.value?.apellido = newApellido
    }

    fun updateCiudad(newCiudad: String) {
        _currentUser.value?.ciudad = newCiudad
    }

    private val _avatarUrl = MutableLiveData("")


    fun login(
        username: String,
        password: String,
        keepLogged: Boolean = false,
        userPrefs: UserPrefs = null!!
    ) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    if (keepLogged) {
                        CoroutineScope(Dispatchers.IO).launch {
                            userPrefs.saveUserData(_loggedUser.value!!, _password.value!!)
                        }
                    }
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get().addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            repo.addUser(
                                User( _avatarUrl.value?:"",
                                    _nombre.value!!, _apellido.value!!,
                                    _ciudad.value!!, _loggedUser.value!!
                                )
                            )
                        }
                    }
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error logging in: ${task.exception}")
                    modShowLoading(false)
                    _showRegisterRequestDialog.value = true
                }
            }
            .addOnFailureListener {
                Log.d("Error", "Error logging in: $it")
                modShowLoading(false)
                _showRegisterRequestDialog.value = true
            }
    }

    fun signOut(context: Context, navController: NavController) {

        val userPrefs = UserPrefs(context)

        CoroutineScope(Dispatchers.IO).launch {
            userPrefs.clearUserData()
        }

        auth.signOut()

        resetUserValues()
        resetMarkerValues(context)
        resetSelectedValues()

        navController.navigate(Routes.LoginScreen.route)
    }

    private fun resetUserValues() {
        _currentUser.value = null
        _markers.value = mutableListOf()
        _posicionActual.value = LatLng(0.0, 0.0)
        _loggedUser.value = ""
        _userId.value = ""
        _nombre.value = ""
        _apellido.value = ""
        _ciudad.value = ""
        _password.value = ""
        //_email.value = ""

        _currentMarker.value = null
        _showBottomSheet.value = false
        _showSaveChangesDialog.value = false
        _showDeleteMarkerDialog.value = false
        _showRegisterRequestDialog.value = false
        _showRegisterDialog.value = false
        _showLoginDialog.value = false
        _showLogOutDialog.value = false

        _goToNext.value = false
        _verContrasena.value = false
        _keepLogged.value = false

        _getUserLocation.value = true
        _prevScreen.value = "MapScreen"
        _errorPass.value = false
        _errorEmail.value = false
        _isLoading.value = true
    }

    fun addMarker(lat: String, long: String, name: String, url: Uri, selectedCategory: String) {
        val markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble()))
        val id = UUID.randomUUID().toString()
        _currentMarker.value = Marker(owner = _loggedUser.value, id, name, markerState, url.toString(), selectedCategory)
        repo.addMarker(_currentMarker.value!!)
    }

    fun uploadImage(imageUri: Uri) {
        repo.uploadImage(imageUri, _currentMarker.value!!)
    }

    fun uploadPfp(imageUri: Uri) {
        repo.uploadPfp(imageUri, _currentUser.value!!)
    }

    fun filterMarkers(
        markers: MutableList<Marker>,
        categoryFilter: String,
        nameFilter: String
    ): List<Marker> {
        val filteredMarkers = markers.filter { marker ->
            (categoryFilter.isEmpty() || marker.categoria.contains(categoryFilter)) &&
                    (nameFilter.isEmpty() || marker.name.contains(nameFilter))
        }
        return filteredMarkers
    }

    fun resetMarkerValues(context: Context) {
        _markerName.value = ""
        _icon.value = ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
        _url.value = Uri.EMPTY
        _inputLat.value = ""
        _inputLong.value = ""
        _markerId.value = ""
    }

    fun resetSelectedValues() {
        _selectedImage.value = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        _selectedUri.value = Uri.EMPTY
    }

    fun updateUser() {
        repo.editUser(_currentUser.value!!)
    }

    fun removeUser() {
        repo.removeUser(_currentUser.value!!)
        repo.deleteUserAuth()
    }

    fun deletePhoto(uri: Uri) {
        repo.deletePhoto(uri)
    }

    fun modCurrentMarker(marker:Marker) {
        _currentMarker.value = marker
    }
}
