package com.example.nala.ui.composables.menus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nala.ui.menus.DrawerItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomDrawer(
    modifier: Modifier,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
    ) {

    Column(
        modifier = modifier,
    ) {
       val elements = listOf(
           DrawerItem.Chronology,
           DrawerItem.Articles,
           DrawerItem.Videos,
           DrawerItem.Settings,
       )

        val backStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry.value?.destination?.route
        Spacer(modifier = Modifier.height(16.dp))
        elements.forEach { item ->
            DrawerElement(
                element = item,
                isSelected = item.route == currentRoute,
                onSelectItem = {
                    navController.navigate(it.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        }
    }

}

@Composable
fun DrawerElement(
    element: DrawerItem,
    isSelected: Boolean,
    onSelectItem: (DrawerItem) -> Unit,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .padding(start = 10.dp)
            .fillMaxWidth()
            .height(45.dp)
            .background(color = if(isSelected) Color.LightGray else Color.White )
            .clickable {
                onSelectItem(element)
                onClick?.let{
                    it()
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = element.icon,
            contentDescription = element.title,
            tint = element.iconColor,
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text=element.title,
            fontSize=18.sp,
            color = Color.DarkGray,
            style = MaterialTheme.typography.h6
        )
    }
}