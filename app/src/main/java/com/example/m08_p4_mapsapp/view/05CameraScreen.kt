package com.example.m08_p4_mapsapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.utils.AskForPermission
import com.example.m08_p4_mapsapp.viewmodel.ViewModel


@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraScreen(vm: ViewModel, navController: NavController) {
    val prevScreen by vm.prevScreen.observeAsState("")
    val context = LocalContext.current
    val isLoading by vm.isLoading.observeAsState(true)
    AskForPermission(
        permission = Manifest.permission.CAMERA,
        onDeclineMsg = "Esta app necesita que le proporciones permisos de cámara para funcionar.",
        isLoading,
        vm
    ) {
        val controller = remember {
            LifecycleCameraController(context).apply {
                CameraController.IMAGE_CAPTURE
            }
        }
        CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomGoBackButton(prevScreen, vm, navController) {
                    vm.showBottomSheet(true)
                }
                TakePhotoButton(context, controller, vm)
                SwitchCameraButton(controller)
            }
        }
    }
}

@Composable
private fun TakePhotoButton(
    context: Context, controller: LifecycleCameraController, vm: ViewModel
) {
    IconButton(
        onClick = {
            vm.takePhoto(context, controller) { photo ->
                vm.modMarkerIcon(photo)
                vm.modUrl(vm.saveBitmapToExternalStorage(context, photo)!!)
            }
        },
    ) {
        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
    }
}

@Composable
private fun SwitchCameraButton(controller: LifecycleCameraController) {
    IconButton(
        onClick = {
            controller.cameraSelector =
                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
        },
    ) { Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch camera") }
}


@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(factory = {
        PreviewView(it).apply {
            this.controller = controller
            controller.bindToLifecycle(lifecycleOwner)
        }
    }, modifier = modifier)
}