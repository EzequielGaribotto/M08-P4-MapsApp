package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.AddMarkerContent
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddMarkerScreen(avm: APIViewModel, navController: NavController) {
    AddMarkerContent(avm, true,  navController)
}
