package com.example.m08_p4_mapsapp.viewmodel


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
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
import java.io.OutputStream
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

    private val _errorPassFormat = MutableLiveData(false)
    val errorPassFormat: LiveData<Boolean> = _errorPassFormat

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

    private val _errorEmailFormat = MutableLiveData(false)
    val errorEmailFormat: LiveData<Boolean> = _errorEmailFormat

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

    private val _markerCategories = MutableLiveData(mutableMapOf<String, Int>())
    val markerCategories: MutableLiveData<MutableMap<String, Int>> = _markerCategories

    init {
        for (r in R.drawable::class.java.declaredFields) {
            if (r.name.startsWith("cat_")) {
                _markerCategories.value?.put(r.name.replace("cat_", ""), r.getInt(r))
            }
        }
    }

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

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userLastName = MutableLiveData<String>()
    val userLastName: LiveData<String> = _userLastName

    private val _userCity = MutableLiveData<String>()
    val userCity: LiveData<String> = _userCity

    private val _userAvatar = MutableLiveData<String>()
    val userAvatar: LiveData<String> = _userAvatar

    fun modUserName(newName: String) {
        _userName.value = newName
    }

    fun modUserLastName(newLastName: String) {
        _userLastName.value = newLastName
    }

    fun modUserCity(newCity: String) {
        _userCity.value = newCity
    }

    fun modSelectedPfpUri(uri: Uri) {
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

    fun modErrorPassFormat(boolean: Boolean) {
        _errorPassFormat.value = boolean
    }

    fun modErrorEmailFormat(boolean: Boolean) {
        _errorEmailFormat.value = boolean
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

    fun removeUser() {
        repo.removeUser(_currentUser.value!!)
        repo.deleteUserAuth()
    }

    fun deletePhoto(uri: Uri) {
        repo.deletePhoto(uri)
    }

    fun modCurrentMarker(marker: Marker) {
        _currentMarker.value = marker
    }

    fun modErrorEmailDupe(b: Boolean) {
        _errorEmailDuplicado.value = b
    }



    fun getUser() {
        repo.getUsers().whereEqualTo("owner", _loggedUser.value).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    _currentUser.value = User(
                        document.getString("avatarUrl") ?: "",
                        document.getString("nombre") ?: "",
                        document.getString("apellido") ?: "",
                        document.getString("ciudad") ?: "",
                        document.getString("owner") ?: ""
                    )
                    _userName.value = _currentUser.value?.nombre
                    _userLastName.value = _currentUser.value?.apellido
                    _userCity.value = _currentUser.value?.ciudad
                    _userAvatar.value = _currentUser.value?.avatarUrl
                    println("User found: ${_currentUser.value}")
                }
            }.addOnFailureListener {
                Log.d("ERROR", "Error getting documents: ", it)
            }
    }

    fun getMarkers() {
        repo.getMarkersFromDatabase().whereEqualTo("owner", _loggedUser.value)
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
                                    latitude.toString().toDouble(), longitude.toString().toDouble()
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
            android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
        } == true
    }

    private val _errorEmailDuplicado = MutableLiveData<Boolean>()
    val errorEmailDuplicado: LiveData<Boolean> = _errorEmailDuplicado

    private val _errorCuentaInexistente = MutableLiveData<Boolean>()
    val errorCuentaInexistente: LiveData<Boolean> = _errorCuentaInexistente

    fun modErrorCuentaInexistente(b: Boolean) {
        _errorCuentaInexistente.value = b
    }

    fun register(
        username: String, password: String, keepLogged: Boolean = false, userPrefs: UserPrefs
    ) {
        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    _goToNext.value = true
                    _isLoading.value = false
                    if (keepLogged) {
                        CoroutineScope(Dispatchers.IO).launch {
                            userPrefs.saveUserData(_email.value!!, _password.value!!)
                        }
                    }
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get().addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                repo.addUser(
                                    User(
                                        _avatarUrl.value ?: "",
                                        _nombre.value!!,
                                        _apellido.value!!,
                                        _ciudad.value!!,
                                        _email.value!!
                                    )
                                )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user : ${task.exception}")
                    if (password.length >= 6) {
                        if (!isValidEmail(username)) {
                            _errorEmailFormat.value = true
                        } else {
                            _errorEmailDuplicado.value = true
                        }
                    } else {
                        _errorPassFormat.value = true
                    }
                    _showRegisterDialog.value = true
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
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
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
                                User(
                                    _avatarUrl.value ?: "",
                                    _nombre.value!!,
                                    _apellido.value!!,
                                    _ciudad.value!!,
                                    _loggedUser.value!!
                                )
                            )
                        }
                    }
                    println("Pasa aquí")
                    _goToNext.value = true

                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error logging in: ${task.exception}")
                    modShowLoading(false)
                    if (!isValidEmail(username)) {
                        _errorEmailFormat.value = true
                    } else {
                        if (password.length >= 6) {
                            _errorCuentaInexistente.value = true
                        } else {
                            _errorPassFormat.value = true
                        }
                    }
                    _showLoginDialog.value = true
                }
            }.addOnFailureListener {
                Log.d("Error", "Error logging in: $it")
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
        _errorPassFormat.value = false
        _errorEmailFormat.value = false
        _isLoading.value = true
    }

    fun addMarker(lat: String, long: String, name: String, url: Uri, selectedCategory: String) {
        val markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble()))
        val id = UUID.randomUUID().toString()
        _currentMarker.value = Marker(
            owner = _loggedUser.value, id, name, markerState, url.toString(), selectedCategory
        )
        repo.addMarker(_currentMarker.value!!)
    }

    fun uploadImage(imageUri: Uri) {
        repo.uploadImage(imageUri, _currentMarker.value!!)
    }

    fun updatePfp(imageUri: Uri) {
        repo.uploadPfp(imageUri, _currentUser.value!!)
    }
    fun updateAvatar(toString: String) {
        _userAvatar.value = toString
    }

    fun filterMarkers(
        markers: MutableList<Marker>, categoryFilter: String, nameFilter: String
    ): List<Marker> {
        val filteredMarkers = markers.filter { marker ->
            (categoryFilter.isEmpty() || marker.categoria.lowercase()
                .contains(categoryFilter.lowercase())) && (nameFilter.isEmpty() || marker.name.lowercase()
                .contains(nameFilter.lowercase()))
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



    fun saveBitmapToExternalStorage(context: Context, bitmap: Bitmap): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, filename)
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        val uri: Uri? =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let { ur1 ->
            val outstream: OutputStream? = context.contentResolver.openOutputStream(ur1)
            outstream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            outstream?.close()
        }

        return uri
    }

    fun takePhoto(
        context: Context, controller: LifecycleCameraController, onPhotoTaken: (Bitmap) -> Unit
    ) {
        controller.takePicture(ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(), 0, 0, image.width, image.height, matrix, true
                    )

                    onPhotoTaken(rotatedBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Error taking photo", exception)
                }
            })
    }

}
