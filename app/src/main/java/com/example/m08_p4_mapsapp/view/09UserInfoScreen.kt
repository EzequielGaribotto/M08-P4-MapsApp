package com.example.m08_p4_mapsapp.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.m08_p4_mapsapp.viewmodel.ViewModel

@Composable
fun UserInfoScreen(vm : ViewModel) {
    val loggedUser by vm.loggedUser.observeAsState("")
    val email by vm.email.observeAsState("")
    val name by vm.nombre.observeAsState("")
    val apellido by vm.apellido.observeAsState("")
    val userId by vm.userId.observeAsState("")
    val user = vm.getUser(userId)
    Text(text =
    "Logged user: $loggedUser\n" +
    "Email: $email\n" +
    "Name: ${user.nombre}\n" +
    "Last name: ${user.apellido}\n" +
            "City: ${user.ciudad}\n"
    )
}