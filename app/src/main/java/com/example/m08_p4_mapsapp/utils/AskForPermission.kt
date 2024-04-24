package com.example.m08_p4_mapsapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

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
                    delay(1000)
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