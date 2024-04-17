package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController, vm: ViewModel) {

    val username by vm.email.observeAsState("")
    val password by vm.password.observeAsState("")
    val showErrorMessage by vm.showErrorMessage.observeAsState(false)
    val userLogin by vm.userLogin.observeAsState(false)
    val userRegister by vm.userRegister.observeAsState(false)
    val goToNext by vm.goToNext.observeAsState(false)
    val prevScreen by vm.prevScreen.observeAsState("")

    Icon(imageVector = Icons.Filled.ArrowBackIosNew,
        contentDescription = "Enrere",
        modifier = Modifier
            .clickable {
                vm.goBackLogin(userLogin, userRegister, navController, prevScreen)
            }
            .padding(16.dp)
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!userLogin && !userRegister) {
            Button(onClick = { vm.modUserRegister(true) }) {
                Text("Registrar")
            }
            Button(onClick = { vm.modUserLogin(true) }) {
                Text("Log-In")
            }
        }
        if (userLogin || userRegister) {
            TextField(
                value = username,
                onValueChange = { vm.modEmail(it) },
                label = { Text("Email") },
                isError = username.isNotEmpty()
            )
            TextField(
                value = password,
                onValueChange = { vm.modPassword(it) },
                label = { Text("Contraseña") },
                isError = password.isNotEmpty()
            )
        }

        if (userLogin) {
            Button(onClick = {
                vm.login(password, username)
                navController.navigate(prevScreen)
            }) {
                Text("Log-In")
            }
        }
        val context = LocalContext.current
        val userprefs = com.example.m08_p4_mapsapp.model.UserPrefs(context)
        if (userRegister) {
            Button(onClick = {
                vm.register(password, username)
                navController.navigate(prevScreen)
                CoroutineScope(Dispatchers.IO).launch {
                    userprefs.saveUserData(username, password)
                }
            }) {
                Text("Registrar")
            }
        }

        if (showErrorMessage) {
            Text(
                "Error al logearse",
                modifier = Modifier.padding(16.dp),
                color = Color.Red,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
        }
    }
}

