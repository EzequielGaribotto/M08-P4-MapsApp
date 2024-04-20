package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.ClickOutsideToDismissKeyboard
import com.example.m08_p4_mapsapp.R
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
    val url by vm.url.observeAsState("")
    val id by vm.markerId.observeAsState("")

    vm.showBottomSheet(false)
    ClickOutsideToDismissKeyboard {
        CustomGoBackButton("MarkerListScreen", vm, navController,
            posteriorActions = { vm.resetMarkerValues(context) })
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SetPhoto(url, icon, vm, lat, long, navController, true)
            SetName(name, vm)
            EditMarker(context, name, lat, long, vm, icon, url, id, navController)
        }
    }
}

@Composable
private fun EditMarker(
    context: Context,
    name: String,
    lat: String,
    long: String,
    vm: ViewModel,
    icon: Bitmap,
    url: String,
    id: String,
    navController: NavController
) {
    val canAddMarker = name.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty()
    Button(onClick = {
        val loggedUser = vm.loggedUser.value

        vm.editMarker(
            marker = Marker(
                owner = loggedUser,
                id = id,
                markerState = MarkerState(LatLng(lat.toDouble(), long.toDouble())),
                name = name,
                icon = icon,
                url = url
            )
        )
        vm.resetMarkerValues(context)
        navController.navigate("MarkerListScreen")
    }, enabled = canAddMarker) {
        Text("Guardar cambios")
    }
}