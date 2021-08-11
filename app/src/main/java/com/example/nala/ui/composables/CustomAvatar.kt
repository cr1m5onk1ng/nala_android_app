package com.example.nala.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun CustomAvatar(
    modifier: Modifier,
    imageUrl: String?,
) {
    Box(modifier = modifier){
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = rememberImagePainter(imageUrl),
            contentDescription = "profile image",
        )
    }
}