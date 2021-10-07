package com.example.nala.ui.composables

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Quicksand

@Composable
fun SmallerButton(
    text: String,
    textColor: Color? = null,
    backgroundColor: Color,
    onCLick: () -> Unit,
    height: Dp,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(100.dp)
            .height(height),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onCLick,
            modifier = Modifier
                .fillMaxWidth()
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400
                    ),
                    color = textColor ?: Color.White,
                )
                Spacer(modifier= Modifier.padding(horizontal = 3.dp))
                Icon(
                    icon,
                    contentDescription = "button icon",
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp)
                )
            }
        }
    }
}