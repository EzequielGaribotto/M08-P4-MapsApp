package com.example.m08_p4_mapsapp


import android.Manifest
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.ui.theme.DarkBlue
import com.example.m08_p4_mapsapp.ui.theme.LightRed
import com.example.m08_p4_mapsapp.ui.theme.LighterGreen160
import com.example.m08_p4_mapsapp.ui.theme.M08P4MapsAppTheme
import com.example.m08_p4_mapsapp.utils.AskForPermission
import com.example.m08_p4_mapsapp.utils.ClickOutsideToDismissKeyboard
import com.example.m08_p4_mapsapp.utils.CustomDialog
import com.example.m08_p4_mapsapp.view.AddMarkerContent
import com.example.m08_p4_mapsapp.view.AddMarkerScreen
import com.example.m08_p4_mapsapp.view.CameraScreen
import com.example.m08_p4_mapsapp.view.EditMarkerScreen
import com.example.m08_p4_mapsapp.view.GalleryScreen
import com.example.m08_p4_mapsapp.view.LoginScreen
import com.example.m08_p4_mapsapp.view.MapScreen
import com.example.m08_p4_mapsapp.view.MarkerListScreen
import com.example.m08_p4_mapsapp.view.RegisterScreen
import com.example.m08_p4_mapsapp.view.UserInfoScreen
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val viewModel by viewModels<ViewModel>()
        setContent {
            M08P4MapsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val isLoading by viewModel.isLoading.observeAsState(true)

                    AskForPermission(
                        permission = Manifest.permission.ACCESS_FINE_LOCATION,
                        onDeclineMsg = "Esta app requiere que le proporciones permisos de localización para funcionar",
                        isLoading,
                        viewModel
                    ) {

                        MyDrawer(viewModel, context)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MyDrawer(vm: ViewModel, context: Context) {
    val navigationController = rememberNavController()
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showLogOutDialog by vm.showLogOutDialog.observeAsState(false)
    CustomDialog(
        show = showLogOutDialog,
        question = "¿Seguro que quieres desconectarte?",
        option1 = "SÍ",
        onOption1Click = {
            scope.launch {
                state.close()
            }
            vm.signOut(context, navigationController)
            vm.showLogOutDialog(false)
        },
        option2 = "NO",
        onOption2Click = { vm.showLogOutDialog(false) }
    )

    ModalNavigationDrawer(drawerState = state, gesturesEnabled = state.isOpen, drawerContent = {
        ModalDrawerSheet {

            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "MenuIcon",
                modifier = Modifier
                    .clickable {
                        scope.launch {
                            state.close()
                        }
                    }
                    .padding(16.dp)
            )
            Text(
                "Menu",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            val screenMap = mapOf(
                "MapScreen" to Icons.Filled.Map,
                "MarkerListScreen" to Icons.AutoMirrored.Filled.List,
                "AddMarkerScreen" to Icons.Filled.Add,
                "UserInfoScreen" to Icons.Filled.AccountCircle
            )

            screenMap.forEach { (screen, icon) ->
                if (currentRoute != null) {
                    CreateNavigationDrawerItem(
                        currentRoute = currentRoute,
                        targetRoute = screen,
                        icon = icon,
                        state = state,
                        scope = scope,
                        navigationController = navigationController
                    )
                }
            }

            // Crea un drawer item para hacer logOut
            val loggedUser by vm.loggedUser.observeAsState("")
            if (loggedUser.isNotEmpty() && loggedUser.isNotBlank()) {
                NavigationDrawerItem(
                    label = { Text("Log Out") },
                    selected = currentRoute == "LoginScreen",
                    onClick = {
                        vm.showLogOutDialog(true)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Log Out"
                        )
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color.White,
                        selectedIconColor = LightRed,
                        unselectedIconColor = LightRed,
                        selectedTextColor = LightRed,
                        unselectedTextColor = LightRed,
                        selectedBadgeColor = LightRed,
                        unselectedBadgeColor = LightRed
                    )
                )
            }
        }
    }) {
        MyScaffold(vm, state, scope, navigationController)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(
    vm: ViewModel,
    state: DrawerState,
    scope: CoroutineScope,
    navigationController: NavHostController,
) {
    val sheetState = rememberModalBottomSheetState()
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomSheet by vm.showBottomSheet.observeAsState(false)
    ClickOutsideToDismissKeyboard {
        Scaffold(topBar = {
            if (currentRoute in arrayOf(
                    "AddMarkerScreen",
                    "MapScreen",
                    "MarkerListScreen",
                    "UserInfoScreen"
                )
            ) {
                if (currentRoute != null) {
                    MyTopAppBar(currentRoute, state, scope, navigationController, vm)
                }
            }
        }) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(
                    navController = navigationController,
                    startDestination = Routes.LoginScreen.route
                ) {

                    composable(Routes.LoginScreen.route) {
                        LoginScreen(
                            navigationController, vm
                        )
                    }

                    composable(Routes.RegisterScreen.route) {
                        RegisterScreen(
                            navigationController, vm
                        )
                    }
                    composable(Routes.MapScreen.route) {
                        MapScreen(vm)
                    }
                    composable(Routes.MarkerListScreen.route) {
                        MarkerListScreen(
                            navigationController, vm,
                        )
                    }
                    composable(Routes.AddMarkerScreen.route) {
                        AddMarkerScreen(
                            vm, navigationController
                        )
                    }
                    composable(Routes.CameraScreen.route) {
                        CameraScreen(vm, navigationController)
                    }
                    composable(Routes.GalleryScreen.route) {
                        GalleryScreen(vm, navigationController)
                    }
                    composable(Routes.EditMarkerScreen.route) {
                        EditMarkerScreen(vm, navigationController)
                    }

                    composable(Routes.UserInfoScreen.route) {
                        UserInfoScreen(vm, navigationController)
                    }
                }
            }

            if (showBottomSheet && vm.prevScreen.value != Routes.AddMarkerScreen.route) {
                ModalBottomSheet(
                    onDismissRequest = {
                        vm.showBottomSheet(false)
                    }, sheetState = sheetState
                ) {
                    ClickOutsideToDismissKeyboard {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End,
                        ) {
                            IconButton(onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        vm.showBottomSheet(false)
                                    }
                                }
                            }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Close")
                            }
                            AddMarkerContent(vm, false, navigationController)
                        }
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
    vm: ViewModel,
) {
    val loggedUser by vm.loggedUser.observeAsState("")
    
    TopAppBar(title = { Text(text = "Los Mapas") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBlue,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.Black,
        ),
        navigationIcon = {
            EnableDrawerButton(state, scope)
        },
        actions = {
            IconButton(onClick = {
                if (loggedUser.isNotEmpty() && loggedUser.isNotBlank()) {
                    vm.showLogOutDialog(true)
                } else {
                    vm.modPrevScreen(currentRoute)
                    navigationController.navigate(Routes.LoginScreen.route)
                }
                scope.launch {
                    state.close()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.run { if (loggedUser != "") ManageAccounts else AccountCircle },
                    contentDescription = "Log In/Log Out"
                )
            }
        }
    )
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

@Composable
fun CreateNavigationDrawerItem(
    currentRoute: String,
    targetRoute: String,
    icon: ImageVector,
    state: DrawerState,
    scope: CoroutineScope,
    navigationController: NavHostController,
) {
    val formattedText = targetRoute.replace(Regex("([A-Z])"), " $1").replace("Screen", "")
    NavigationDrawerItem(
        label = {
            Text(text = formattedText)
        },
        icon = { Icon(imageVector = icon, contentDescription = formattedText) },
        selected = currentRoute == targetRoute,
        onClick = {
            if (currentRoute != targetRoute) {
                scope.launch {
                    state.close()
                }
                navigationController.navigate(targetRoute)
            }
        },
        shape = MaterialTheme.shapes.medium,
        colors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = LighterGreen160,
            selectedTextColor = LighterGreen160,
            selectedBadgeColor = LighterGreen160,
        )
    )
    HorizontalDivider()
}