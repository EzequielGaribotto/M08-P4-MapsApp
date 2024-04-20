package com.example.m08_p4_mapsapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.ClickOutsideToDismissKeyboard
import com.example.m08_p4_mapsapp.R
import com.example.m08_p4_mapsapp.model.UserPrefs
import com.example.m08_p4_mapsapp.navigation.Routes
import com.example.m08_p4_mapsapp.viewmodel.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    ClickOutsideToDismissKeyboard {
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
                    vm, email, password, errorEmail, errorPass, keepLogged, userPrefs, goToNext
                )
                CustomClickableText(
                    "¿Ya tienes una? ", "Iniciar Sesión", "LoginScreen", navController, vm
                )
                //GoogleRegister(clientLauncher(vm, navController, keepLogged, userPrefs, context, storedUserData, validLogin, goToNext))
            }

            InvalidRegisterDialog(showRegisterDialog, vm)
            SuccessfulRegisterDialog(successfulRegister, vm)
        }
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
private fun GoogleRegister(pair: Pair<ManagedActivityResultLauncher<Intent, ActivityResult>, GoogleSignInClient>) {
    Row(
        modifier = Modifier.clickable { pair.first.launch(pair.second.signInIntent) },
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google),
            contentDescription = "Google icon",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Registrarse con Google",
            fontSize = 24.sp,
        )
    }
}

@Composable
private fun clientLauncher(
    vm: ViewModel,
    navController: NavController,
    keepLogged: Boolean,
    userPrefs: UserPrefs,
    context: Context,
    storedUserData: State<List<String>>,
    validLogin: Boolean,
    goToNext: Boolean
): Pair<ManagedActivityResultLauncher<Intent, ActivityResult>, GoogleSignInClient> {
    //val token = BuildConfig.TOKEN
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            vm.signInWithGoogleCredential(credential) {
                vm.modShowLoading(true)
                navController.navigate(Routes.MapScreen.route)
            }
            if (account.email != null) vm.modificarLoggedUser(account.email!!)
            if (keepLogged) {
                CoroutineScope(Dispatchers.IO).launch {
                    userPrefs.saveUserData(vm.getLoggedUser(), "")
                }
            }
        } catch (e: Exception) {
            Log.d("GOOGLE_SIGNIN", "GoogleSign failed")
        }
    }

    val opciones = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
        //.requestIdToken(token)
        .requestEmail().build()
    val googleSignInCliente = GoogleSignIn.getClient(context, opciones)

    if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != "" && storedUserData.value[1] != "" && validLogin) {
        vm.modShowLoading(false)
        vm.login(storedUserData.value[0], storedUserData.value[1], keepLogged, userPrefs)
        if (goToNext) {
            navController.navigate(Routes.MapScreen.route)
        }
    } else if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != "") {
        vm.modShowLoading(false)
        launcher.launch(googleSignInCliente.signInIntent)

    }
    return Pair(launcher, googleSignInCliente)
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
    goToNext: Boolean
) {
    Button(
        onClick = {
            vm.modErrorEmail(!vm.isValidEmail(email))
            vm.modErrorPass(!vm.isValidPass(pass))
            if (!(errorEmail || errorPass)) {
                vm.register(email, pass, keepLogged, userPrefs)
                if (goToNext) {
                    vm.showSuccessfulRegisterDialog(true)
                }
            } else {
                vm.showRegisterDialog(true)
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
fun InvalidRegisterDialog(show: Boolean, vm: ViewModel) {
    if (show) {
        Dialog(onDismissRequest = { vm.showRegisterDialog(false) }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                val message = StringBuilder()
                if (vm.errorPass.value == true) message.appendLine("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
                if (vm.errorEmail.value == true) message.appendLine("Ya existe una cuenta con este email, o es inválido")
                Text(text = message.toString().trim())
            }
        }
    }
}