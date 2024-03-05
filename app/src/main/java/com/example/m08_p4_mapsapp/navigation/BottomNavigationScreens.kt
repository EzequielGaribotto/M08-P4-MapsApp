package com.example.m08_p4_mapsapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationScreens(val route:String, val icon: ImageVector, val label:String) {
    object ListScreen:BottomNavigationScreens(Routes.ListScreen.route, Icons.Filled.Home, "List Screen")
    object FavsScreen:BottomNavigationScreens(Routes.FavsScreen.route, Icons.Filled.Favorite, "Favs Screen")
}