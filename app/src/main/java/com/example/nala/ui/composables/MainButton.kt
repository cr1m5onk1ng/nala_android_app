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
import androidx.compose.ui.unit.dp

@Composable
fun MainButton(
    text: String,
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
                Icon(Icons.Rounded.Add, contentDescription = "add to review")
                Spacer(modifier= Modifier.padding(horizontal = 5.dp))
                Text(text)
            }
        }
    }
}