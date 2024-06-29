package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.utils.CustomDialog
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.utils.CustomButton
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditMarkerScreen(vm: ViewModel, navController: NavController) {
    val lat by vm.inputLat.observeAsState("")
    val long by vm.inputLong.observeAsState("")
    val name by vm.markerName.observeAsState("")
    val context = LocalContext.current
    val img: Bitmap = ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
    val icon by vm.icon.observeAsState(img)
    val url by vm.url.observeAsState(Uri.EMPTY)
    val id by vm.markerId.observeAsState("")
    val showSaveChangesDialog by vm.showSaveChangesDialog.observeAsState(false)
    val loggedUser by vm.loggedUser.observeAsState("")
    val selectedCategory by vm.category.observeAsState("")

    vm.showBottomSheet(false)

    CustomGoBackButton(
        "MarkerListScreen",
        vm,
        navController,
        after = { vm.resetMarkerValues(context) })
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SetPhoto(url, icon, vm, lat, long, navController, true)
        TextField(modifier = Modifier.padding(15.dp), value = name, onValueChange = { vm.modMarkerName(it) }, label = { Text("Nombre") })
        EditCategory(vm, selectedCategory)
        EditMarker(name, lat, long, vm, selectedCategory)
    }

    CustomDialog(show = showSaveChangesDialog,
        question = "¿Seguro que quieres guardar los cambios?",
        option1 = "SÍ",
        onOption1Click = {
            vm.showSaveChangesDialog(false)
            vm.uploadImage(url)
            vm.editMarker(
                marker = Marker(
                    owner = loggedUser,
                    id = id,
                    markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble())),
                    name = name,
                    icon = icon,
                    url = url.toString(),
                    categoria = selectedCategory
                )
            )
            vm.resetMarkerValues(context)
            navController.navigate("MarkerListScreen")
        },
        option2 = "No",
        onOption2Click = {
            vm.showSaveChangesDialog(false)
        })
}

@Composable
fun EditCategory(vm: ViewModel, selectedCategory: String) {
    val categories by vm.markerCategories.observeAsState(emptyMap())
    Row { MarkerCategories(categories, vm) }
    Text("Categoría seleccionada: ${selectedCategory.ifEmpty { "Ninguna" }}")
}

@Composable
private fun EditMarker(
    name: String, lat: String, long: String, vm: ViewModel, selectedCategory: String
) {
    val canAddMarker =
        name.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty() && selectedCategory.isNotEmpty()
    CustomButton(onClick = {
        vm.showSaveChangesDialog(true)
    }, enabled = canAddMarker) {
        Text("Guardar cambios")
    }
}