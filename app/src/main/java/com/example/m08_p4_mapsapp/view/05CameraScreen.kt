package com.example.m08_p4_mapsapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraScreen(avm: APIViewModel, navController: NavController) {

    // CAMERA PERMISSIONS
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    val context = LocalContext.current
    val isCameraPermissionGranted by avm.cameraPermissionGranted.observeAsState(false)
    val shouldShowPermissionRationale by avm.shouldShowPermissionRationale.observeAsState(false)
    val showPermissionDenied by avm.showPermissionDenied.observeAsState(false)
    val controller = remember {
        LifecycleCameraController(context).apply {
            CameraController.IMAGE_CAPTURE
        }
    }

    CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
    IconButton(
        onClick = {
            controller.cameraSelector =
                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
        },
    ) {
        Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch camera")

    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            IconButton(
                onClick = {
                    takePhoto(context, controller) { photo -> avm.updateMarkerIcon(photo) }
                    avm.switchPhotoTaken(true)
                },
            ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
            }

            IconButton(
                onClick = {
                    avm.switchBottomSheet(true)
                    navController.navigate(Routes.MapScreen.route)
                },
            ) {
                Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "Go Back")
            }
        }
    }
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

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Error taking photo", exception)
            }
        }
    )
}