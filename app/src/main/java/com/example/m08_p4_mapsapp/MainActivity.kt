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
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.m08_p4_mapsapp.ui.theme.M08P45MapsAppTheme
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
import com.example.m08_p4_mapsapp.view.DetailScreen
import com.example.m08_p4_mapsapp.view.FavsScreen
import com.example.m08_p4_mapsapp.view.ListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiViewModel by viewModels<APIViewModel>()
        val bottomNavigationItems = listOf(
            BottomNavigationScreens.ListScreen,
            BottomNavigationScreens.FavsScreen,
        )
        setContent {
            M08P45MapsAppTheme {
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
    val state:DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(drawerState = state, gesturesEnabled = true, drawerContent = {
        ModalDrawerSheet {
            Text("Drawer title", modifier = Modifier.padding(16.dp))
            Divider()
        }
    }) {
        MyScaffold(myViewModel, state, bottomNavigationItems)
    }
}

@Composable
fun MyScaffold(myViewModel: APIViewModel, state: DrawerState, bottomNavigationItems:List<BottomNavigationScreens>) {
    val navigationController = rememberNavController()
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar = {
            if (currentRoute != null) {
                MyTopAppBar(myViewModel, currentRoute)
            }
        },
        bottomBar = {
            if (currentRoute != null) {
                MyBottomBar(navigationController, bottomNavigationItems, currentRoute)
            }
        })
    { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navigationController,
                startDestination = Routes.ListScreen.route
            ) {
                composable(Routes.ListScreen.route) {
                    ListScreen(
                        navigationController, myViewModel
                    )
                }
                composable(Routes.FavsScreen.route) {
                    FavsScreen(
                        navigationController, myViewModel
                    )
                }
                composable(Routes.DetailScreen.route) {
                    DetailScreen(
                        myViewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun MyBottomBar(navigationController: NavController, bottomNavigationItems: List<BottomNavigationScreens>, currentRoute: String) {
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
fun MyTopAppBar(apiViewModel: Any, currentRoute: Any) {
    if (currentRoute == Routes.ListScreen.route) {

        TopAppBar(title = { Text(text = "") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = LightGreen,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.Black,
            ), actions = {

            })
    }
}
