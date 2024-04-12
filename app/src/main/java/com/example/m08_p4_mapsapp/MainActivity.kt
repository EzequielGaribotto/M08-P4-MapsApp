package com.example.m08_p4_mapsapp


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.ui.theme.LightGreen
import com.example.m08_p4_mapsapp.ui.theme.M08P4MapsAppTheme
import com.example.m08_p4_mapsapp.view.AddMarkerContent
import com.example.m08_p4_mapsapp.view.AddMarkerScreen
import com.example.m08_p4_mapsapp.view.CameraScreen
import com.example.m08_p4_mapsapp.view.GalleryScreen
import com.example.m08_p4_mapsapp.view.LoginScreen
import com.example.m08_p4_mapsapp.view.MapScreen
import com.example.m08_p4_mapsapp.view.MarkerListScreen
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        super.onCreate(savedInstanceState)
        val apiViewModel by viewModels<APIViewModel>()
        setContent {
            M08P4MapsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    GeoPermission(apiViewModel, context)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GeoPermission(avm: APIViewModel, context:Context) {
    val permissionState =
        rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    if (permissionState.status.isGranted) {
        MyDrawer(myViewModel = avm, context)
    }
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MyDrawer(myViewModel: APIViewModel, context: Context) {
    val navigationController = rememberNavController()
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val prevRoute = myViewModel.prevScreen.value


    ModalNavigationDrawer(drawerState = state, gesturesEnabled = state.isOpen, drawerContent = {
        ModalDrawerSheet {

            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "MenuIcon",
                modifier = Modifier.clickable {
                    scope.launch {
                        state.close()
                    }
                })
            Text(
                "Menu",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Divider()
            NavigationDrawerItem(label = { Text(text = "Map") }, selected = false, onClick = {
                if (currentRoute != "MapScreen") {
                    scope.launch {
                        state.close()
                    }
                    navigationController.navigate(Routes.MapScreen.route)
                }
                if (prevRoute == "AddMarkerScreen") {
                    myViewModel.resetMarkerValues(context)
                }
            })
            Divider()
            NavigationDrawerItem(label = { Text(text = "Marker List") },
                selected = false,
                onClick = {
                    if (currentRoute != "MarkerListScreen") {
                        scope.launch {
                            state.close()
                        }
                        myViewModel.switchPhotoTaken(false)
                        navigationController.navigate("MarkerListScreen")
                    }
                }
            )
            Divider()
            NavigationDrawerItem(label = { Text(text = "Add Marker") },
                selected = false,
                onClick = {
                    if (currentRoute != Routes.AddMarkerScreen.route) {
                        scope.launch {
                            state.close()
                        }
                        navigationController.navigate(Routes.AddMarkerScreen.route)
                    }
                }
            )
        }
    }) {
        MyScaffold(myViewModel, state, scope, navigationController, context)
    }
}


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(
    avm: APIViewModel,
    state: DrawerState,
    scope: CoroutineScope,
    navigationController: NavHostController, context: Context
) {
    val sheetState = rememberModalBottomSheetState()
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomSheet by avm.showBottomSheet.observeAsState(false)
    Scaffold(topBar = {
        if (currentRoute != null && currentRoute != Routes.LoginScreen.route && currentRoute != Routes.CameraScreen.route) {
            MyTopAppBar(currentRoute, state, scope, navigationController, avm)
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
                        navigationController, avm
                    )
                }
                composable(Routes.MapScreen.route) {
                    MapScreen(
                        navigationController, avm
                    )
                }
                composable(Routes.MarkerListScreen.route) {
                    MarkerListScreen(
                        navigationController, avm,
                    )
                }
                composable(Routes.AddMarkerScreen.route) {
                    AddMarkerScreen(
                        avm, navigationController
                    )
                }
                composable(Routes.CameraScreen.route) {
                    CameraScreen(avm, navigationController)
                }
                composable(Routes.GalleryScreen.route) {
                    GalleryScreen(avm, navigationController)
                }
            }
        }

        if (showBottomSheet && avm.prevScreen.value != Routes.AddMarkerScreen.route) {
            ModalBottomSheet(
                onDismissRequest = {
                    avm.switchBottomSheet(false)
                    avm.resetMarkerValues(context)
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
                                avm.switchBottomSheet(false)
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Close")
                    }
                    if (currentRoute != null) {
                        AddMarkerContent(avm, false, navigationController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    currentRoute: String,
    state: DrawerState,
    scope: CoroutineScope,
    navigationController: NavHostController,
    avm: APIViewModel
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
                scope.launch {
                    state.close()
                }
                avm.modPrevScreen(currentRoute)
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

