package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.CustomButton
import com.example.m08_p4_mapsapp.model.UserPrefs
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import java.lang.StringBuilder

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(navController: NavController, vm: ViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isLoading by vm.isLoading.observeAsState(true)
    val goToNext by vm.goToNext.observeAsState(false)
    val email by vm.email.observeAsState("")
    val password by vm.password.observeAsState("")
    val nombre by vm.nombre.observeAsState("")
    val apellido by vm.apellido.observeAsState("")
    val ciudad by vm.ciudad.observeAsState("")
    val verContrasena by vm.verContrasena.observeAsState(false)
    val keepLogged by vm.keepLogged.observeAsState(false)

    val errorEmail by vm.errorEmail.observeAsState(false)
    val errorPass by vm.errorPass.observeAsState(false)
    val showRegisterDialog by vm.showRegisterDialog.observeAsState(false)
    val successfulRegister by vm.successfulRegister.observeAsState(false)
    val context = LocalContext.current
    val userPrefs = UserPrefs(context)

    if (goToNext) {
        navController.navigate(Routes.MapScreen.route)
        vm.modGoToNext(false)
    }
    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp), color = MaterialTheme.colorScheme.secondary
            )
        }
        if (goToNext) {
            navController.navigate(Routes.MapScreen.route)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DatosTextField(nombre, "Nombre", vm::modificarNombreState)
            DatosTextField(apellido, "Apellido", vm::modificarApellidoState)
            DatosTextField(ciudad, "Ciudad", vm::modificarCiudadState)
            EmailTextfield(email, vm, keyboardController)
            PasswordTextfield(password, vm, verContrasena)
            KeepMeLoggedInCheckbox(keepLogged, vm)
            RegisterButton(
                vm, email, password, errorEmail, errorPass, keepLogged, userPrefs
            )
            CustomClickableText(
                "¿Ya tienes una? ", "Iniciar Sesión", "LoginScreen", navController, vm
            )
        }

        InvalidRegisterDialog(showRegisterDialog, vm)
        SuccessfulRegisterDialog(successfulRegister, vm)
    }
}

@Composable
fun SuccessfulRegisterDialog(successfulRegister: Boolean, vm: ViewModel) {
    if (successfulRegister) {
        Dialog(onDismissRequest = { vm.showSuccessfulRegisterDialog(false) }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "¡Registro exitoso!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DatosTextField(value: String, label: String, onValueChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
    )
}

@Composable
private fun RegisterButton(
    vm: ViewModel,
    email: String,
    pass: String,
    errorEmail: Boolean,
    errorPass: Boolean,
    keepLogged: Boolean,
    userPrefs: UserPrefs,
) {
    CustomButton(
        onClick = {
            vm.modErrorEmail(!vm.isValidEmail(email))
            if (errorEmail || errorPass) {
                vm.showRegisterDialog(true)
            } else {
                vm.register(email, pass, keepLogged, userPrefs)
            }
        }, modifier = Modifier.fillMaxWidth(), enabled = email.isNotEmpty() && pass.isNotEmpty()
    ) {
        Text(
            text = "Registrar", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White
        )
    }
}

@Composable
fun KeepMeLoggedInCheckbox(
    keepLogged: Boolean, vm: ViewModel
) {
    Row {
        Text(
            text = "Permanecer conectado",
            Modifier.align(CenterVertically),
            color = Color.Black,
            fontSize = 16.sp
        )
        Checkbox(checked = keepLogged, onCheckedChange = { keep -> vm.modKeepLogged(keep) })
    }
}

@Composable
fun InvalidRegisterDialog(showRegisterDialog: Boolean, vm: ViewModel) {
    if (showRegisterDialog) {
        Dialog(onDismissRequest = {
            vm.showRegisterDialog(false)
            vm.modErrorPass(false)
            vm.modErrorEmail(false)
            vm.modErrorEmailDuplicado(false)
        }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                val message = StringBuilder()
                if (vm.errorPass.value == true) message.appendLine("Contraseña inválida\nLa contraseña debe tener como mínimo 6 caracteres.")
                if (vm.errorEmail.value == true) message.appendLine("El email tiene un formato incorrecto")
                if (vm.errorEmailDuplicado.value == true) message.appendLine("Ya existe una cuenta con este email")
                Text(text = message.toString().trim())
            }
        }
    }
}