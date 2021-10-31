package com.example.nala.ui.menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object HomeNavItem : BottomNavItem("home_screen", Icons.Default.Home)
    object ReviewsNavItem : BottomNavItem("review_screen", Icons.Default.List)

}
