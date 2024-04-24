package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import com.example.m08_p4_mapsapp.CustomDialog
import com.example.m08_p4_mapsapp.CustomButton
import com.example.m08_p4_mapsapp.model.UserPrefs
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import java.lang.StringBuilder

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(navController: NavController, vm: ViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val email by vm.email.observeAsState("")
    val password by vm.password.observeAsState("")
    val errorEmail by vm.errorEmail.observeAsState(false)
    val errorPass by vm.errorPass.observeAsState(false)
    val showLoginDialog by vm.showLoginDialog.observeAsState(false)
    val verContrasena by vm.verContrasena.observeAsState(false)
    val keepLogged by vm.keepLogged.observeAsState(false)
    val showRegisterRequestDialog by vm.showRegisterRequestDialog.observeAsState(false)
    val goToNext by vm.goToNext.observeAsState(false)

    val context = LocalContext.current
    val userPrefs = UserPrefs(context)
    val storedUserData = userPrefs.getUserData.collectAsState(initial = emptyList())
    println("Stored user data: ${storedUserData.value}")
    useStoredData(storedUserData, vm, keepLogged, userPrefs, navController)
    if (goToNext) {
        navController.navigate(Routes.MapScreen.route)
        vm.modGoToNext(false)
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmailTextfield(email, vm, keyboardController)
        PasswordTextfield(password, vm, verContrasena)
        KeepMeLoggedInCheckbox(keepLogged, vm)
        LogInButton(vm, email, password, errorEmail, errorPass, keepLogged, userPrefs)
        CustomClickableText(
            "¿No tienes cuenta? ",
            "Regístrate",
            "RegisterScreen",
            navController,
            vm
        )
        InvalidLoginDialog(showLoginDialog, vm)
        CustomDialog(
            show = showRegisterRequestDialog,
            question = "Parece que aún no te has registrado.\n¿Deseas registrarte?",
            option1 = "SÍ",
            onOption1Click = {
                vm.showRegisterRequestDialog(false)
                navController.navigate("RegisterScreen")
            },
            option2 = "NO",
            onOption2Click = { vm.showRegisterRequestDialog(false) }
        )
    }


}

private fun useStoredData(
    storedUserData: State<List<String>>,
    vm: ViewModel,
    keepLogged: Boolean,
    userPrefs: UserPrefs,
    navController: NavController
) {
    if (storedUserData.value.isNotEmpty() &&
        storedUserData.value[0] != "" && storedUserData.value[1] != ""
    ) {
        vm.goToNext.value = true
        vm.modShowLoading(true)
        vm.login(storedUserData.value[0], storedUserData.value[1], keepLogged, userPrefs)
        navController.navigate("MapScreen")
        vm.modShowLoading(false)
    } else {
        vm.modShowLoading(false)
    }
}

@Composable
fun CustomClickableText(
    normalText: String,
    clickableText: String,
    route: String,
    navController: NavController,
    vm: ViewModel
) {
    val text = buildAnnotatedString {
        pushStyle(style = SpanStyle(color = Color.Black, fontSize = 20.sp))
        append(normalText)
        pushStyle(style = SpanStyle(color = Color.Blue, fontSize = 22.sp))
        append(clickableText)
        pop()
    }

    ClickableText(
        text = text,
        onClick = { offset ->
            vm.showRegisterRequestDialog(false)
            vm.showLoginDialog(false)
            vm.showRegisterDialog(false)

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
                vm.showLoginDialog(true)
            } else {
                vm.login(email, pass, keepLogged, userPrefs)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = email.isNotEmpty() && pass.isNotEmpty()
    ) {
        Text(
            text = "Login", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
        )
    }
}

@Composable
fun InvalidLoginDialog(show: Boolean, vm: ViewModel) {
    if (show && (vm.errorEmail.value == true || vm.errorPass.value == true)) {
        Dialog(onDismissRequest = {
            vm.showLoginDialog(false)
            vm.modErrorPass(false)
            vm.modErrorEmail(false)
        }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                val message = StringBuilder()
                if (vm.errorPass.value == true) message.appendLine("Contraseña inválida o incorrecta")
                if (vm.errorEmail.value == true) message.appendLine("El email no es válido")

                Text(text = message.toString().trim())
            }
        }
    }
}