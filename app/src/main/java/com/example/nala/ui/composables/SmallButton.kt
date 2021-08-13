package com.example.nala.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SmallButton(
    text: String,
    textColor: Color? = null,
    backgroundColor: Color,
    icon: ImageVector? = null,
    onCLick: () -> Unit,
    height: Dp,
    width: Dp? = null,
) {
    val displayedIcon = icon ?: Icons.Rounded.Add
    Row(
        modifier =
            if(width == null)
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(height) else
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(height)
                        .width(width),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onCLick,
            modifier = Modifier
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(18.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColor,
                contentColor = textColor ?: Color.Black,
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    displayedIcon,
                    contentDescription = "some icon"
                )
                Spacer(modifier= Modifier.padding(horizontal = 5.dp))
                Text(text)
            }
        }
    }
}