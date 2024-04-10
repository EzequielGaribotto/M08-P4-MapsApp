package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.viewmodel.APIViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController, avm: APIViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val username by avm.email.observeAsState("")
        val password by avm.password.observeAsState("")
        TextField(value = username,
            onValueChange = { avm.modEmail(it) },
            label = { Text("Email") })
        TextField(
            value = password,
            onValueChange = {
                avm.modPassword(it)
            },
            label = { Text("Contraseña") }
        )
        val userLogin by avm.userLogin.observeAsState(false)
        val userRegister by avm.userRegister.observeAsState(false)
        if (!userLogin && !userRegister) {
            Button(onClick = { avm.modUserRegister(true)}) {
                Text("Registrar")
            }
            Button(onClick = { avm.modUserLogin(true)}) {
                Text("Log-In")
            }
        }

        if (userLogin) {
            Button(onClick = {
                avm.login(password,username)
                navController.navigate("MapScreen")
            }) {
                Text("Log-In")
            }
        }
        if (userRegister) {
            Button(onClick = {
                avm.register(password,username)
                navController.navigate("MapScreen")
            }) {
                Text("Registrar")
            }
        }

    }
}