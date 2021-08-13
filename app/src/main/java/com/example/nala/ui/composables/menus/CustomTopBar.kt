package com.example.nala.ui.composables.menus

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.ui.theme.Blue500
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomTopBar(
    title: String, 
    scope: CoroutineScope, 
    scaffoldState: ScaffoldState,
    navController: NavController,
){
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                style = MaterialTheme.typography.h6,
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "drawer",
                    tint = Color.White,
                )
            }
        },
        backgroundColor = Blue500,
        contentColor = Color.White,
        actions = {
            IconButton(
                onClick = { 
                    navController.navigate("home_screen")
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "home",
                    tint = Color.White,
                )
            }
        }
    )
}