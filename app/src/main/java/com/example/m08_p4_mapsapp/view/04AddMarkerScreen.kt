package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.utils.CustomButton
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.ViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddMarkerScreen(vm: ViewModel, navController: NavController) {
    vm.modPrevScreen("AddMarkerScreen")
    AddMarkerContent(vm, true, navController)
}

@Composable
fun AddMarkerContent(
    vm: ViewModel, markerScreen: Boolean = false, navigationController: NavController
) {
    val lat by vm.inputLat.observeAsState("")
    val long by vm.inputLong.observeAsState("")
    val name by vm.markerName.observeAsState("")
    val context = LocalContext.current
    val emptyIcon: Bitmap =
        ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
    val icon by vm.icon.observeAsState(emptyIcon)
    val url by vm.url.observeAsState(Uri.EMPTY)
    val photoTaken = if (url == Uri.EMPTY) !icon.sameAs(emptyIcon) else true
    val categories by vm.markerCategories.observeAsState(emptyMap())
    val selectedCategory by vm.category.observeAsState("")
    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.run { if (markerScreen) Center else Top },
        ) {
            SetPhoto(url, icon, vm, lat, long, navigationController, photoTaken)
            Row(
                modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly

            ) { MarkerCategories(categories, vm) }
            Text("Categoría seleccionada: ${selectedCategory.ifEmpty { "Ninguna" }}")
            SetData(name, vm, lat, long)
            AddMarker(
                photoTaken,
                selectedCategory,
                name,
                lat,
                long,
                vm,
                url,
                navigationController,
                context
            )
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun SetPhoto(
    url: Uri,
    icon: Bitmap?,
    vm: ViewModel,
    lat: String,
    long: String,
    navigationController: NavController,
    photoTaken: Boolean
) {
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    GlideImage(model = if (url.takeIf { it != Uri.EMPTY } != null) url else icon!!,
        contentDescription = "Marker Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(240.dp)
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(15.dp))
    )
    CustomButton(onClick = {
        if (currentRoute != null) {
            vm.modPrevScreen(currentRoute)
        }
        vm.showBottomSheet(false)
        vm.modPosicionActual(
            lat.toDoubleOrNull() ?: 0.0, long.toDoubleOrNull() ?: 0.0
        )
        navigationController.navigate(Routes.CameraScreen.route)
    }) {
        Text((if (photoTaken) "Retomar foto" else "Tomar foto"))
    }


    CustomButton(onClick = {
        if (currentRoute != null) {
            vm.modPrevScreen(currentRoute)
        }
        vm.showBottomSheet(false)
        vm.modPosicionActual(
            lat.toDoubleOrNull() ?: 0.0, long.toDoubleOrNull() ?: 0.0
        )
        navigationController.navigate(Routes.GalleryScreen.route)
    }) {
        Text("Buscar foto en galería")
    }
}


@Composable
fun SetData(
    name: String, vm: ViewModel, lat: String, long: String
) {
    TextField(value = name, onValueChange = { vm.modMarkerName(it) }, label = { Text("Nombre") })
    TextField(value = lat,
        onValueChange = { vm.modInputLat(it) },
        label = { Text("Latitud") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
    TextField(value = long,
        onValueChange = { vm.modInputLong(it) },
        label = { Text("Longitud") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}
@Composable
fun MarkerCategories(markerCategories: Map<String, Int>, vm: ViewModel) {
    markerCategories.forEach { category ->
        IconButton(onClick = {
            vm.modCategory(category.key)
        }) {
            Image(
                painterResource(id = category.value),
                contentDescription = category.key,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            )
        }
    }
}

@Composable
fun AddMarker(
    photoTaken: Boolean,
    selectedCategory: String,
    name: String,
    lat: String,
    long: String,
    vm: ViewModel,
    url: Uri,
    navigationController: NavController,
    context: Context
) {
    val canAddMarker =
        photoTaken && name.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty() && selectedCategory.isNotEmpty()
    CustomButton(
        modifier = Modifier.padding(10.dp), onClick = {
            vm.addMarker(lat, long, name, url, selectedCategory)
            vm.uploadImage(url)
            vm.showBottomSheet(false)

            if (vm.prevScreen.value == "AddMarkerScreen") {
                navigationController.navigate(Routes.MapScreen.route)
            } else {
                vm.resetMarkerValues(context)
            }
        }, enabled = canAddMarker
    ) {
        Text("Agregar marcador")
    }
}

