package com.example.nala.ui.composables.menus

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.network.model.menus.ActionModel
import com.example.nala.ui.theme.Blue500
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomTopBar(
    title: String,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    navIcon: ImageVector? = null,
    navIconAction: (() -> Unit)? = null,
    iconColor: Color? = null,
    textStyle: TextStyle? = null,
    actions: List<ActionModel>? = null,
    elevation: Dp? = null,
    scope: CoroutineScope? = null,
    scaffoldState: ScaffoldState,
    navController: NavController,
){
    TopAppBar(
        elevation = elevation ?: 0.dp,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                style = textStyle ?: MaterialTheme.typography.h5,
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                if(navIconAction != null) {
                    navIconAction()
                } else {
                    scope?.launch{
                        scaffoldState.drawerState.open()
                    }
                }
             }) {
                Icon(
                    imageVector = navIcon ?: Icons.Filled.Menu,
                    contentDescription = "nav button",
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