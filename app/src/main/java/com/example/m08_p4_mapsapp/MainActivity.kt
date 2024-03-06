package com.example.m08_p4_mapsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.m08_p4_mapsapp.ui.theme.M08P4MapsAppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel
import com.example.m08_p4_mapsapp.navigation.BottomNavigationScreens
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.ui.theme.DarkerGreen
import com.example.m08_p4_mapsapp.ui.theme.IntermediateGreen
import com.example.m08_p4_mapsapp.ui.theme.LightGreen
import com.example.m08_p4_mapsapp.view.AddMarkerScreen
import com.example.m08_p4_mapsapp.view.MarkerListScreen
import com.example.m08_p4_mapsapp.view.MapScreen
import com.example.m08_p4_mapsapp.view.LoginScreen
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
                    MyDrawer(apiViewModel, bottomNavigationItems)
                }
            }
        }
    }
}

@Composable
fun MapScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val itb = LatLng(41.4534265, 2.1837151)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(itb, 10f)
        }
        GoogleMap(modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                TODO()
            },
            onMapLongClick = {
                TODO()
            }) {
            Marker(
                state = MarkerState(position = itb),
                title = "ITB",
                snippet = "Marker at ITB",
            )
        }
    }
}


@Composable
fun MyDrawer(myViewModel: APIViewModel, bottomNavigationItems: List<BottomNavigationScreens>) {
    val navigationController = rememberNavController()
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(drawerState = state, gesturesEnabled = true, drawerContent = {
        ModalDrawerSheet {

            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Search")
            Text("Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineLarge)
            Divider()
            NavigationDrawerItem(label = { Text(text = "Map") },
                selected = false,
                onClick = {
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
            NavigationDrawerItem(label = { Text(text =  "Add Marker") },
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


@Composable
fun MyScaffold(
    myViewModel: APIViewModel,
    state: DrawerState,
    bottomNavigationItems: List<BottomNavigationScreens>,
    scope: CoroutineScope,
    navigationController:NavHostController
) {
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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
                        myViewModel,
                    )
                }
                composable(Routes.AddMarkerScreen.route) {
                    AddMarkerScreen(
                        myViewModel,
                    )
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