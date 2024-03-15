package com.example.m08_p4_mapsapp.navigation


sealed class Routes(val route: String) {
    object LoginScreen : Routes("LoginScreen")
    object MapScreen : Routes("MapScreen")
    object MarkerListScreen : Routes("MarkerListScreen")
    object AddMarkerScreen : Routes("AddMarkerScreen")
    object CameraScreen : Routes("CameraScreen")
}