package com.example.m08_p4_mapsapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.m08_p4_mapsapp.navigation.BottomNavigationScreens
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.ui.theme.DarkerGreen
import com.example.m08_p4_mapsapp.ui.theme.IntermediateGreen
import com.example.m08_p4_mapsapp.ui.theme.LightGreen
import com.example.m08_p4_mapsapp.ui.theme.M08P4MapsAppTheme
import com.example.m08_p4_mapsapp.view.AddMarkerScreen
import com.example.m08_p4_mapsapp.view.LoginScreen
import com.example.m08_p4_mapsapp.view.MapScreen
import com.example.m08_p4_mapsapp.view.MarkerListScreen
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiViewModel by viewModels<APIViewModel>()
        val bottomNavigationItems = listOf(
            BottomNavigationScreens.MapScreen,
            BottomNavigationScreens.MarkerListScreen,
            BottomNavigationScreens.AddMarkerScreen,
        )
        setContent {
            M08P4MapsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    GeoPermission(apiViewModel, bottomNavigationItems)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GeoPermission(avm: APIViewModel, bottomNavigationItems: List<BottomNavigationScreens>) {
    val permissionState =
        rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    if (permissionState.status.isGranted) {
        MyDrawer(myViewModel = avm, bottomNavigationItems = bottomNavigationItems)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(myViewModel: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        val fusedLocationProviderClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }
        val marcadorActual by myViewModel.marcadorActual.observeAsState()
        val cameraPositionState = rememberCameraPositionState {
            position = marcadorActual?.let { CameraPosition.fromLatLngZoom(it, 18f) }!!
        }
        val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
        locationResult.addOnCompleteListener(context as MainActivity) { task ->
            if (task.isSuccessful) {
                myViewModel.modMarcadorActual(task.result.latitude, task.result.longitude)
            } else {
                Log.e("Error", "Exception: %s", task.exception)
            }
        }
        GoogleMap(modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = {
                myViewModel.modInputLat(it.latitude.toString())
                myViewModel.modInputLong(it.longitude.toString())
                myViewModel.switchBottomSheet(true)
            }) {
            val markers by myViewModel.markers.observeAsState(mutableListOf())
            markers?.forEach {
                Marker(
                    state = it.markerState,
                    title = it.name,
                    snippet = "Marker at ${it.markerState.position.latitude}, ${it.markerState.position.longitude}",
                )
            }
        }
    }
}


@Composable
fun MyDrawer(myViewModel: APIViewModel, bottomNavigationItems: List<BottomNavigationScreens>) {
    val navigationController = rememberNavController()
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet {

            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Search")
            Text(
                "Menu",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Divider()
            NavigationDrawerItem(label = { Text(text = "Map") }, selected = false, onClick = {
                scope.launch {
                    state.close()
                }
                navigationController.navigate(Routes.MapScreen.route)
            })
            Divider()
            NavigationDrawerItem(label = { Text(text = "Marker List") },
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    navigationController.navigate(Routes.MarkerListScreen.route)
                })
            Divider()
            NavigationDrawerItem(label = { Text(text = "Add Marker") },
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    navigationController.navigate(Routes.AddMarkerScreen.route)
                })

        }
    }) {
        MyScaffold(myViewModel, state, bottomNavigationItems, scope, navigationController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(
    myViewModel: APIViewModel,
    state: DrawerState,
    bottomNavigationItems: List<BottomNavigationScreens>,
    scope: CoroutineScope,
    navigationController: NavHostController
) {
    val sheetState = rememberModalBottomSheetState()
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomSheet by myViewModel.showBottomSheet.observeAsState(false)
    Scaffold(topBar = {
        if (currentRoute != null) {
            MyTopAppBar(currentRoute, state, scope, navigationController)
        }
    }, bottomBar = {
        if (currentRoute != null) {
            MyBottomBar(navigationController, bottomNavigationItems, currentRoute)
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navigationController, startDestination = Routes.MapScreen.route
            ) {

                composable(Routes.LoginScreen.route) {
                    LoginScreen(
                        navigationController, myViewModel
                    )
                }
                composable(Routes.MapScreen.route) {
                    MapScreen(
                        navigationController, myViewModel
                    )
                }
                composable(Routes.MarkerListScreen.route) {
                    MarkerListScreen(
                        navigationController, myViewModel,
                    )
                }
                composable(Routes.AddMarkerScreen.route) {
                    AddMarkerScreen(
                        myViewModel,
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    myViewModel.switchBottomSheet(false)
                }, sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End,
                ) {
                    IconButton(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                myViewModel.switchBottomSheet(false)
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Close")
                    }
                    AddMarkerContent(myViewModel, false)
                }
            }
        }
    }
}

@Composable
fun MyBottomBar(
    navigationController: NavController,
    bottomNavigationItems: List<BottomNavigationScreens>,
    currentRoute: String
) {
    BottomNavigation(backgroundColor = LightGreen, contentColor = Color.White) {
        bottomNavigationItems.forEach { item ->
            BottomNavigationItem(icon = { Icon(item.icon, contentDescription = item.label) },
                selected = currentRoute == item.route,
                enabled = currentRoute != item.route,
                label = { Text(item.label) },
                selectedContentColor = DarkerGreen,
                unselectedContentColor = IntermediateGreen,
                alwaysShowLabel = false,
                onClick = { navigationController.navigate(item.route) })
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    currentRoute: String,
    state: DrawerState,
    scope: CoroutineScope,
    navigationController: NavHostController
) {
    if (currentRoute != Routes.LoginScreen.route) {

        TopAppBar(title = { Text(text = "Los Mapas") }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LightGreen,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.Black,

            ), navigationIcon = {
            EnableDrawerButton(state, scope)
        }, actions = {
            IconButton(onClick = {
                navigationController.navigate(Routes.LoginScreen.route)
            }) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "User")
            }
        })
    }
}

@Composable
private fun EnableDrawerButton(state: DrawerState, scope: CoroutineScope) {
    IconButton(onClick = {
        scope.launch {
            state.open()
        }
    }) {
        Icon(imageVector = Icons.Filled.Menu, contentDescription = "Search")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddMarkerContent(avm: APIViewModel, markerScreen: Boolean = false) {
    val lat by avm.inputLat.observeAsState("")
    val long by avm.inputLong.observeAsState("")
    val name by avm.markerName.observeAsState("")
    val defaultIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val icon by avm.icon.observeAsState(defaultIcon)


    // CAMERA
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            CameraController.IMAGE_CAPTURE
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            IconButton(
                onClick = {
                    takePhoto(context, controller) { photo ->
                        avm.updateMarkerIcon(photo)
                    }
                },
            ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
            }
        }
        Column(
            modifier = Modifier
                .padding(top = 80.dp)
            .fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.run { if (markerScreen) Center else Top },
        ) {
            TextField(value = name,
                onValueChange = { avm.modMarkerName(it) },
                label = { Text("Nombre") })
            TextField(value = lat,
                onValueChange = { avm.modInputLat(it) },
                label = { Text("Latitud") })
            TextField(value = long,
                onValueChange = { avm.modInputLong(it) },
                label = { Text("Longitud") })

            Button(onClick = { avm.addMarker(lat, long, name, icon) }, enabled = icon != defaultIcon) {
                Text("Agregar marcador")
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
                val matrix = Matrix().apply{
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