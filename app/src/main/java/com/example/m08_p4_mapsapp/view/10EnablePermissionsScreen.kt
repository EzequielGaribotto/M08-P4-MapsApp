package com.example.m08_p4_mapsapp.view

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.m08_p4_mapsapp.CustomDialog
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EnablePermissionsScreen(vm: ViewModel, navController: NavController) {
    val prevScreen by vm.prevScreen.observeAsState("")
    val notGrantedPermission by vm.notGrantedPermission.observeAsState(mutableListOf())
    val grantPermissions by vm.grantPermissions.observeAsState(false)
    val showGrantPermissionsDIalog by vm.showGrantPermissionsDialog.observeAsState(false)

    CustomDialog(
        show = showGrantPermissionsDIalog,
        question = "¿Quieres conceder los permisos?",
        option1 = "SÍ",
        onOption1Click = {
            vm.modShowGrantPermissionsDialog(false)
            vm.modGrantPermissions(true)
        },
        option2 = "NO",
        onOption2Click = {
            vm.modShowGrantPermissionsDialog(false)
            navController.navigate(prevScreen)
        }
    )
    if (grantPermissions) {
        notGrantedPermission.forEach { permission ->
            val permissionState = rememberPermissionState(permission)
            LaunchedEffect(permissionState) {
                if (!permissionState.status.isGranted) {
                    permissionState.launchPermissionRequest()
                    vm.removeNotGrantedPermission(permission)
                }
            }
            notGrantedPermission.ifEmpty { navController.navigate(prevScreen) }
        }
    }
}
