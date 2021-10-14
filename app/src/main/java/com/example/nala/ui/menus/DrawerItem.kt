package com.example.nala.ui.menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nala.ui.theme.*


sealed class DrawerItem(val route: String, val icon: ImageVector, val iconColor: Color, val title: String) {
    object Home : DrawerItem("home_screen", Icons.Filled.Home, Blue600, "Home")
    object Chronology : DrawerItem("chronology", Icons.Filled.History, Color.Gray,"Chronology")
    object Articles : DrawerItem("articles", Icons.Filled.Article, Green, "Articles")
    object Videos : DrawerItem("videos", Icons.Filled.VideoLibrary, VideoRed, "Videos")
    object Settings : DrawerItem("settings", Icons.Filled.Settings, Color.DarkGray, "Settings")
}

