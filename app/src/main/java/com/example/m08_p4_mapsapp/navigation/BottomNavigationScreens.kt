package com.example.m08_p4_mapsapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationScreens(val route:String, val icon: ImageVector, val label:String) {
    object MapScreen:BottomNavigationScreens(Routes.MapScreen.route, Icons.Filled.Home, "Map Screen")
    object MarkerListScreen:BottomNavigationScreens(Routes.MarkerListScreen.route, Icons.Filled.Favorite, "Marker List Screen")
    object AddMarkerScreen:BottomNavigationScreens(Routes.AddMarkerScreen.route, Icons.Filled.Favorite, "Add Marker Screen")
}