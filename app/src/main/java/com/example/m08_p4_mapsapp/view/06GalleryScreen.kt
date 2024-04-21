package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.viewmodel.ViewModel

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GalleryScreen(vm: ViewModel, navController: NavController) {
    val prevScreen by vm.prevScreen.observeAsState("")
    val context = LocalContext.current
    val emptyImg: Bitmap = ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmapOrNull()!!
    val selectedImage by vm.selectedImage.observeAsState(emptyImg)
    val selectedUri by vm.selectedUri.observeAsState(Uri.EMPTY)
    val launchImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            vm.modSelectedUri(uri)
            vm.modSelectedImage(
                if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            })
        }
    }

    CustomGoBackButton(prevScreen, vm, navController,
        before = {
            if (prevScreen == "MapScreen") {
                vm.showBottomSheet(true)
                vm.resetSelectedValues()
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            launchImage.launch("image/*")
        }) {
            Text(text = "Abrir Galería")
        }
        Image(
            bitmap = selectedImage.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(
                    CircleShape
                )
                .size(250.dp)
                .background(Color.Transparent)
                .border(width = 1.dp, color = Color.White, shape = CircleShape)
        )
        Button(
            onClick = {
                vm.showBottomSheet(true)
                vm.modMarkerIcon(selectedImage)
                vm.modUrl(selectedUri)
                if (prevScreen != null) {
                    navController.navigate(prevScreen)
                }
            },
            enabled = !selectedImage.sameAs(emptyImg)
        ) {
            Text(text = "Establecer como icono")
        }
    }
}

@Composable
fun CustomGoBackButton(
    prevScreen: String,
    vm: ViewModel,
    navController: NavController,
    before: () -> Unit = {},
    after: () -> Unit = {}
) {
    Icon(imageVector = Icons.Filled.ArrowBackIosNew,
        contentDescription = "Enrere",
        modifier = Modifier
            .clickable {
                before()
                vm.goBack(navController, prevScreen)
                after()
            }
            .padding(16.dp)
    )
}