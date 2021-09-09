package com.example.nala.ui.composables

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Quicksand

@Composable
fun MainButton(
    text: String,
    textColor: Color? = null,
    textSize: TextUnit? = null,
    backgroundColor: Color,
    onCLick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
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
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = text,
                    modifier = Modifier
                        .size(14.dp)
                        .background(color=Color.White)
                )
                Spacer(modifier= Modifier.padding(horizontal = 5.dp))
                Text(
                    text,
                    style = TextStyle(
                        color=textColor ?: Color.Black,
                        fontFamily = Quicksand,
                        fontSize = textSize ?: 14.sp,
                        fontWeight = FontWeight.W500,
                    )
                )
            }
        }
    }
}