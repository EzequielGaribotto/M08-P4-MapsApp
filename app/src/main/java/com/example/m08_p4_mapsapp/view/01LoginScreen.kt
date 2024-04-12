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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController, avm: APIViewModel) {

    val username by avm.email.observeAsState("")
    val password by avm.password.observeAsState("")
    val showErrorMessage by avm.showErrorMessage.observeAsState(false)
    val userLogin by avm.userLogin.observeAsState(false)
    val userRegister by avm.userRegister.observeAsState(false)
    val goToNext by avm.goToNext.observeAsState(false)
    val prevScreen by avm.prevScreen.observeAsState("")

    Icon(imageVector = Icons.Filled.ArrowBackIosNew,
        contentDescription = "Enrere",
        modifier = Modifier
            .clickable {
                avm.goBack(userLogin, userRegister, navController, prevScreen)
            }
            .padding(16.dp)
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!userLogin && !userRegister) {
            Button(onClick = { avm.modUserRegister(true) }) {
                Text("Registrar")
            }
            Button(onClick = { avm.modUserLogin(true) }) {
                Text("Log-In")
            }
        }
        if (userLogin || userRegister) {
            TextField(
                value = username,
                onValueChange = { avm.modEmail(it) },
                label = { Text("Email") },
                isError = !avm.isValidEmail(username) && username.isNotEmpty()
            )
            TextField(
                value = password,
                onValueChange = { avm.modPassword(it) },
                label = { Text("Contraseña") },
                isError = !avm.isValidPassword(password) && password.isNotEmpty()
            )
        }

        if (userLogin) {
            Button(onClick = {
                avm.login(password, username)
                if (goToNext) navController.navigate(prevScreen)
                else avm.modShowErrorMessage(true)
            }, enabled = avm.isValidEmail(username) && avm.isValidPassword(password)) {
                Text("Log-In")
            }
        }

        if (userRegister) {
            Button(onClick = {
                avm.register(password, username)
                if (goToNext) navController.navigate(prevScreen)
                else avm.modShowErrorMessage(true)
            }, enabled = avm.isValidEmail(username) && avm.isValidPassword(password)) {
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

