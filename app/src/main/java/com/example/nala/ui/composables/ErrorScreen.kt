package com.example.nala.ui.composables


import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nala.R
import com.example.nala.ui.theme.Blue700

@Composable
fun ErrorScreen(
    text: String,
    subtitle: String = "",
    action: (() -> Unit)? = null,
    actionName: String? = null,
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
                text = actionName ?: R.string.snackbar_retry.toString(),
                backgroundColor = Blue700,
                onCLick = { action() },
                height = 50.dp,
                icon = Icons.Filled.Refresh,
            )
        }
    }
}