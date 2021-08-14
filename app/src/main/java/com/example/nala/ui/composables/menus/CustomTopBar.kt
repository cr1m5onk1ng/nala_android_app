package com.example.nala.ui.composables.menus

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.network.model.menus.ActionModel
import com.example.nala.ui.theme.Blue500
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomTopBar(
    title: String,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    iconColor: Color? = null,
    textStyle: TextStyle? = null,
    actions: List<ActionModel>? = null,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
){
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                style = textStyle ?: MaterialTheme.typography.h5,
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
                    tint = iconColor ?: Color.White,
                )
            }
        },
        backgroundColor = backgroundColor ?: Blue500,
        contentColor = contentColor ?: Color.White,
        actions = {
            actions?.let{ acts ->
                acts.map{
                    IconButton(
                        onClick = it.action
                    ) {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = "action",
                            tint = if(it.isActive) Color.Red else iconColor ?: Color.White,
                        )
                    }
                }
            }
            IconButton(
                onClick = { 
                    navController.navigate("home_screen")
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "home",
                    tint = iconColor ?: Color.White,
                )
            }
        }
    )
}