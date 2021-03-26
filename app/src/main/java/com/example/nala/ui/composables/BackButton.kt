package com.example.nala.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BackButton(
    navController: NavController,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ){
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(
                Icons.Rounded.ArrowBack,
                modifier = Modifier
                    .size(26.dp),
                contentDescription = "arrow_back",
                tint = Color.Black
            )
        }
    }
}