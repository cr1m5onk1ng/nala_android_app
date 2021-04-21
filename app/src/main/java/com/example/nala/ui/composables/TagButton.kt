package com.example.nala.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Quicksand

@Composable
fun TagButton(
    text: String,
    textColor: Color? = null,
    textSize: TextUnit? = null,
    textWeight: FontWeight? = null,
    height: Dp,
    width: Dp? = null,
    padding: Dp? = null,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(height)
            .width(width?: Dp.Unspecified)
            .padding(padding ?: 3.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(18.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontSize = textSize ?: 12.sp,
                    fontWeight = textWeight ?: FontWeight.Light,
                    color = textColor ?: Color.Black
                )
            )
        }
    }
}