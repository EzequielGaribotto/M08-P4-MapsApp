package com.example.m08_p4_mapsapp.utils

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.m08_p4_mapsapp.viewmodel.ViewModel

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