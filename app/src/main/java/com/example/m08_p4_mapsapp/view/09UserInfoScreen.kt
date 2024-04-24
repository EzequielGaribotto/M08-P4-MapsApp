package com.example.m08_p4_mapsapp.view

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_p4_mapsapp.CustomDialog
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.viewmodel.ViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun UserInfoScreen(vm: ViewModel, navController: NavController) {
    val context = LocalContext.current
    val emptyImg: Bitmap =
        ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
    val selectedPfp by vm.selectedPfp.observeAsState(emptyImg)
    val selectedUri by vm.selectedPfpUri.observeAsState(Uri.EMPTY)
    val user by vm.currentUser.observeAsState()
    val showSaveUserChangesDialog by vm.showSaveUserChangesDialog.observeAsState(false)
    val showDeleteUserDialog by vm.showDeleteUserDialog.observeAsState(false)
    vm.getUser()
    val galleryLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            vm.modPfpUri(uri)
            vm.modSelectedPfp(
                if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            context.contentResolver,
                            uri
                        )
                    )
                }
            )
        }
    }

    if (user != null) {
        val userName = remember { mutableStateOf(user!!.nombre) }
        val userLastName = remember { mutableStateOf(user!!.apellido) }
        val userCity = remember { mutableStateOf(user!!.ciudad) }
        val userAvatar = remember { mutableStateOf(user!!.avatarUrl) }.value.toUri()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Button(onClick = {
                    galleryLaunch.launch("image/*")
                }) {
                    Text(text = "Abrir Galería")
                }
            }

            GlideImage(
                model = selectedUri.toString().ifEmpty { userAvatar.toString().ifEmpty { selectedPfp }},
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(width = 1.dp, color = Color.White, shape = CircleShape),
                contentScale = ContentScale.Crop
            )

            UserTextField(
                label = "Nombre",
                value = userName.value,
                onValueChange = { newValue ->
                    userName.value = newValue
                    vm.updateNombre(newValue)
                }
            )
            UserTextField(
                label = "Apellido",
                value = userLastName.value,
                onValueChange = { newValue ->
                    userLastName.value = newValue
                    vm.updateApellido(newValue)
                }
            )
            UserTextField(
                label = "Ciudad",
                value = userCity.value,
                onValueChange = { newValue ->
                    userCity.value = newValue
                    vm.updateCiudad(newValue)
                }
            )


            Button(
                onClick = {
                    vm.showSaveUserChangesDialog(true)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp)
            ) {
                Text("Guardar cambios", fontSize = 16.sp)
            }
            Button(
                onClick = {
                    vm.showDeleteUserDialog(true)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp)
            ) {
                Text("Desactivar cuenta", fontSize = 16.sp)
            }
        }
        CustomDialog(
            show = showSaveUserChangesDialog,
            question = "¿Quieres cambiar los datos del usuario?",
            option1 = "Si",
            onOption1Click = {
                println("1USUARIO: NOMBRE: ${userName.value}, APELLIDO: ${userLastName.value}, CIUDAD: ${userCity.value}, AVATAR: ${selectedUri.toString()}")
                vm.uploadPfp(selectedUri)
                Thread.sleep(1000)
                println("2USUARIO: NOMBRE: ${userName.value}, APELLIDO: ${userLastName.value}, CIUDAD: ${userCity.value}, AVATAR: ${selectedUri.toString()}")
                vm.updateUser()
                println("3USUARIO: NOMBRE: ${userName.value}, APELLIDO: ${userLastName.value}, CIUDAD: ${userCity.value}, AVATAR: ${selectedUri.toString()}")
                vm.showSaveUserChangesDialog(false)
            },
            option2 = "No",
            onOption2Click = { vm.showSaveUserChangesDialog(false) }
        )
        CustomDialog(
            show = showDeleteUserDialog,
            question = "¿Estás seguro de que quieres desactivar tu cuenta?",
            option1 = "Sí",
            onOption1Click = {
                vm.showDeleteUserDialog(false)
                vm.removeUser()
                vm.signOut(context, navController)
            },
            option2 = "No",
            onOption2Click = { vm.showDeleteUserDialog(false) }
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(50.dp),
            )
            Text("Loading user info...", modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun UserTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}