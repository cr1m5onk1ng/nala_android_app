package com.example.nala.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Blue700
import com.example.nala.ui.theme.Quicksand

@Composable
fun CustomTextButton(
    text: String,
    textSize: TextUnit? = null,
    textColor: Color? = null,
    textWeight: FontWeight? = null,
    padding: Dp? = null,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(padding ?: 3.dp)
    ){
        Text(
            text,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = textSize ?: 14.sp,
                fontWeight = textWeight ?: FontWeight.W500,
                color = textColor ?: Blue700,
                textDecoration = TextDecoration.Underline
            ),
        )
    }
}