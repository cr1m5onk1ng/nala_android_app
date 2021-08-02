package com.example.nala.ui.composables

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BackButton(
    navController: NavController,
    modifier: Modifier = Modifier,
    cleanupFunction: (() -> Unit?)? = null,
) {
    val activity = (LocalContext.current as? Activity)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ){
        IconButton(
            onClick = {

                cleanupFunction?.let {
                    it()
                }
                if(navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    activity!!.finish()
                }
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