package com.example.nala.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Quicksand

@Composable
fun ErrorScreen(text: String, subtitle: String) {
    Column (
        modifier = Modifier
            .padding(32.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 22.sp,
                fontWeight = FontWeight.W500,
                color = Color.White
            )
        )
        if(subtitle.isNotEmpty()) {
            Text(
                subtitle,
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.White
                )
            )
        }

    }
}