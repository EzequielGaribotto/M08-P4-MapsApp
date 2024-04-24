package com.example.m08_p4_mapsapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.m08_p4_mapsapp.ui.theme.DarkBlue
import com.example.m08_p4_mapsapp.ui.theme.MediumBlue

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CutCornerShape(30),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MediumBlue,
        contentColor = Color.White
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(15.dp),
    border: BorderStroke? = BorderStroke(5.dp, DarkBlue).takeIf { enabled }?: BorderStroke(5.dp, Color.Gray),
    contentPadding: PaddingValues = PaddingValues(all = 12.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}