package com.example.nala.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nala.ui.composables.SmallerButton
import com.example.nala.ui.theme.Blue700
import com.example.nala.ui.theme.Quicksand

@Composable
fun ErrorScreen(
    text: String,
    subtitle: String = "",
    action: (() -> Unit)? = null,
) {
    Column (
        modifier = Modifier
            .padding(32.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text,
            style = MaterialTheme.typography.subtitle1
        )
        if(subtitle.isNotEmpty()) {
            Text(
                subtitle,
                style = MaterialTheme.typography.body1,
            )
        }
        action?.let{
            Spacer(modifier = Modifier.height(16.dp))
            SmallerButton(
                text = "Retry",
                backgroundColor = Blue700,
                onCLick = { action() },
                height = 50.dp,
                icon = Icons.Filled.Refresh,
            )
        }
    }
}