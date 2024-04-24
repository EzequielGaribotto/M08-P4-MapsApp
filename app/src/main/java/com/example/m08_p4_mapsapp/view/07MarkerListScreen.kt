package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.m08_p4_mapsapp.ClickOutsideToDismissKeyboard
import com.example.m08_p4_mapsapp.CustomDialog
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.ui.theme.DarkBrown
import com.example.m08_p4_mapsapp.ui.theme.LightBrown
import com.example.m08_p4_mapsapp.viewmodel.ViewModel

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MarkerListScreen(navController: NavController, vm: ViewModel) {
    var categoryFilter by remember { mutableStateOf("") }
    var nameFilter by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    val markers: MutableList<Marker> by vm.markers.observeAsState(mutableListOf())
    vm.getMarkers()
    val filteredMarkers = markers.filter { marker ->
        (categoryFilter.isEmpty() || marker.categoria.contains(categoryFilter)) &&
                (nameFilter.isEmpty() || marker.name.contains(nameFilter))
    }

    ClickOutsideToDismissKeyboard {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(Modifier.fillMaxWidth()) {
                IconButton(onClick = { showFilters = !showFilters }) {
                    val icon =
                        if (showFilters) Icons.Filled.FilterList else Icons.Filled.FilterListOff
                    Icon(icon, contentDescription = "Filtro")
                }
                if (showFilters) {
                    Row(Modifier.fillMaxWidth()) {
                        TextField(
                            value = categoryFilter,
                            onValueChange = { categoryFilter = it },
                            label = {
                                Text("Filtrar por categoría", fontSize = 14.sp)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        TextField(
                            value = nameFilter,
                            onValueChange = { nameFilter = it },
                            label = { Text("Filtrar por nombre", fontSize = 14.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (filteredMarkers.isNotEmpty()) {
                LazyColumn {
                    items(filteredMarkers) { marker ->
                        MarkerItem(marker, vm, navController, onClickGo = { lat, long ->
                            vm.modPosicionActual(lat, long)
                            navController.navigate(Routes.MapScreen.route)
                        })
                    }
                }
            } else {
                Text("No se encontraron marcadores")
            }
        }
    }
}

@Composable
fun MarkerItem(
    marker: Marker, vm: ViewModel, navController: NavController, onClickGo: (Double, Double) -> Unit
) {
    val deleteMarkerDialog by vm.showDeleteMarkerDialog.observeAsState(false)
    val deletingMarker by vm.deletingMarker.observeAsState(marker)
    val lat = marker.getMarkerState().position.latitude
    val long = marker.getMarkerState().position.longitude
    val photo = marker.getIcon()
    val name = marker.getName()
    val id = marker.getId()
    val category = marker.getCategoria()
    val categories by vm.markerCategories.observeAsState(mapOf())
    vm.getMarkerCategories()

    Card(
        border = BorderStroke(2.dp, DarkBrown),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Box(modifier = Modifier
            .background(LightBrown)
            .clickable {
                onClickGo(lat, long)
            }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(marker.getUri())
                        .crossfade(true).build(),
                    contentDescription = "Foto del marcador",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                )
                val categoryId = categories[category]
                if (categoryId != null) {
                    Image(
                        painterResource(id = categoryId),
                        contentDescription = category,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Text(
                        "Lat. $lat\n" +
                                "Long. $long\n" +
                                "Categoría: $category",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                }
            }
            Icon(imageVector = Icons.Filled.Close,
                contentDescription = "Eliminar marcador",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        vm.modDeletingMarker(marker)
                        vm.showDeleteMarkerDialog(true)
                    })
            Icon(imageVector = Icons.Filled.Edit,
                contentDescription = "Editar marcador",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        vm.modMarkerName(name)
                        vm.modMarkerIcon(photo)
                        vm.modUrl(marker.getUri())
                        vm.modInputLat(lat.toString())
                        vm.modInputLong(long.toString())
                        vm.modMarkerId(id)
                        vm.modCategory(category)
                        vm.modPrevScreen("MarkerListScreen")
                        vm.showBottomSheet(false)
                        navController.navigate(Routes.EditMarkerScreen.route)
                    })
            CustomDialog(show = deleteMarkerDialog,
                question = "¿Estás seguro de que quieres eliminar el marcador \"${deletingMarker!!.name}\"?",
                option1 = "SI",
                onOption1Click = {
                    vm.showDeleteMarkerDialog(false)
                    vm.removeMarker(deletingMarker!!)
                },
                option2 = "NO",
                onOption2Click = { vm.showDeleteMarkerDialog(false) })
        }
    }
}