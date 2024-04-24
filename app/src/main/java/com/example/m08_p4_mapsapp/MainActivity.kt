package com.example.m08_p4_mapsapp


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material.icons.rounded.CheckCircle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
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
                        permission = android.Manifest.permission.ACCESS_FINE_LOCATION,
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
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForPermission(
    permission: String,
    onDeclineMsg: String = "Esta app requiere que le proporciones permisos",
    isLoading: Boolean,
    vm: ViewModel,
    onAccept: @Composable () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission)

    if (!permissionState.status.isGranted && !isLoading) {
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
    }

    when {
        isLoading -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1000)
                    vm.modShowLoading(false)
                }
            }
        }

        permissionState.status.isGranted -> {
            onAccept()
        }

        else -> {
            PermissionDeclinedScreen(onDeclineMsg)
        }
    }
}

@Composable
fun PermissionDeclinedScreen(message: String = "Esta app requiere que le proporciones permisos") {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
    ) {
        Text(
            text = "Permisos necesarios",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Text(text = message, textAlign = TextAlign.Center)
        CustomButton(onClick = {
            val activity = context as Activity
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", activity.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            activity.startActivity(intent)
        }) {
            Text(text = "Aceptar")
        }

        IconButton(onClick = { (context as Activity).recreate() }) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Recargar"
            )
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

@Composable
fun CustomDialog(
    show: Boolean,
    question: String,
    option1: String,
    onOption1Click: () -> Unit,
    option2: String,
    onOption2Click: () -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onOption2Click) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = question,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomButton(onClick = onOption1Click) {
                        Text(
                            text = option1,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    CustomButton(onClick = onOption2Click) {
                        Text(
                            text = option2,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClickOutsideToDismissKeyboard(content: @Composable () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                keyboardController?.hide()
            }
    ) {
        content()
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