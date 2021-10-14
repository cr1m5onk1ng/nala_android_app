package com.example.nala.ui.composables

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun BottomBar(
    navController: NavController,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
) {
    BottomNavigation(
        elevation = 12.dp
    ) {
        BottomNavigationItem(
            selected = isHomeSelected,
            onClick = {
                if (!isHomeSelected) {
                    toggleHome(true)
                    toggleReviews(false)
                    navController.navigate("home_screen") {
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
                }},
            icon = {Icon(
                Icons.Rounded.Home,
                contentDescription="home")
            }
        )
        BottomNavigationItem(
            selected = isReviewsSelected,
            onClick = {
                if(!isReviewsSelected) {
                    toggleHome(false)
                    toggleReviews(true)

                    navController.navigate("review_screen") {
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
                } },
            icon = {Icon(
                Icons.Rounded.List,
                contentDescription="home")
            }
        )
    }
}
