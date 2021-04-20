package com.example.nala.ui.composables

import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Blue700
import com.example.nala.ui.theme.Quicksand

@Composable
fun CustomTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
    ){
        Text(
            text,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Blue700,
                textDecoration = TextDecoration.Underline
            ),
        )
    }
}