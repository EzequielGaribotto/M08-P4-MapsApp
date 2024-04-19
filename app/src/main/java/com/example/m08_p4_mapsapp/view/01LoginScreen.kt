package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.model.UserPrefs
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(navController: NavController, vm: ViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isLoading: Boolean by vm.isLoading.observeAsState(true)
    val goToNext: Boolean by vm.goToNext.observeAsState(false)
    val email: String by vm.email.observeAsState("")
    val password: String by vm.password.observeAsState("")
    val errorEmail by vm.errorEmail.observeAsState(false)
    val errorPass by vm.errorPass.observeAsState(false)
    val showLoginDialog: Boolean by vm.showLoginDialog.observeAsState(false)
    val verContrasena: Boolean by vm.verContrasena.observeAsState(false)
    val keepLogged: Boolean by vm.keepLogged.observeAsState(false)

    val context = LocalContext.current
    val userPrefs = UserPrefs(context)

    if (!isLoading) {
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
            EmailTextfield(email, vm, keyboardController)
            PasswordTextfield(password, vm, verContrasena)
            KeepMeLoggedInCheckbox(keepLogged, vm)
            LogInButton(vm, email, password, errorEmail, errorPass, keepLogged, userPrefs)
            CustomClickableText("¿No tienes cuenta? ", "Regístrate", "RegisterScreen", navController)
        }
        InvalidLoginDialog(showLoginDialog, vm) { vm.showDialogLogin(false) }
    }
}

@Composable
fun CustomClickableText(
    normalText: String,
    clickableText: String,
    route: String,
    navController: NavController
) {
    val text = buildAnnotatedString {
        pushStyle(style = SpanStyle(color = Color.Black, fontSize = 24.sp))
        append(normalText)
        pushStyle(style = SpanStyle(color = Color.Blue, fontSize = 24.sp))
        append(clickableText)
        pop()
    }

    ClickableText(
        text = text,
        onClick = { offset ->
            if (offset in normalText.length until text.length) {
                navController.navigate(route)
            }
        }
    )
}

@Composable
fun EmailTextfield(
    emailState: String, vm: ViewModel, keyboardController: SoftwareKeyboardController?
) {
    TextField(
        value = emailState,
        onValueChange = { vm.modificarEmailState(it) },
        maxLines = 1,
        label = { Text(text = "Email") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
    )
}

@Composable
fun PasswordTextfield(
    passwordState: String, vm: ViewModel, passwordVisibility: Boolean
) {
    TextField(
        value = passwordState,
        onValueChange = { vm.modificarPasswordState(it) },
        label = { Text(text = "Password") },
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (!passwordVisibility) {
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { vm.modVerContrasena(!passwordVisibility) }) {
                Icon(imageVector = image, contentDescription = "Password visibility")
            }
        },
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}

@Composable
private fun LogInButton(
    vm: ViewModel,
    emailState: String,
    passwordState: String,
    errorEmail: Boolean,
    errorPass: Boolean,
    keepLogged: Boolean,
    userPrefs: UserPrefs
) {
    Button(
        onClick = {
            vm.modInvalidEmail(!vm.isValidEmail(emailState))
            vm.modInvalidPass(!vm.isValidPass(passwordState))
            if (!(errorEmail || errorPass)) {
                if (keepLogged) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(vm.pillarLoggedUser(), "")
                    }
                }
                vm.login(emailState, passwordState)
            } else {
                vm.showDialogLogin(true)
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Login", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
        )
    }
}

@Composable
fun InvalidLoginDialog(show: Boolean, vm: ViewModel, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                val text = StringBuilder()
                if (vm.errorPass.value == true) text.appendLine("Contraseña incorrecta")
                if (vm.errorEmail.value == true) text.appendLine("El email no es válido")
                Text(text = text.toString().trim())
            }
        }
    }
}