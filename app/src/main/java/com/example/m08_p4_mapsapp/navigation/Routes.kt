package com.example.m08_p4_mapsapp.navigation


sealed class Routes(val route: String) {
    data object LoginScreen : Routes("LoginScreen")
    data object RegisterScreen : Routes("RegisterScreen")
    data object MapScreen : Routes("MapScreen")
    data object MarkerListScreen : Routes("MarkerListScreen")
    data object AddMarkerScreen : Routes("AddMarkerScreen")
    data object CameraScreen : Routes("CameraScreen")
    data object GalleryScreen : Routes("GalleryScreen")
    data object EditMarkerScreen : Routes("EditMarkerScreen")
    data object UserInfoScreen : Routes("UserInfoScreen")
}