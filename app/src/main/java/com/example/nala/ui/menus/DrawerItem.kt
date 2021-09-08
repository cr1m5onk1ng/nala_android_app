package com.example.nala.ui.menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nala.ui.theme.*


sealed class DrawerItem(val route: String, val icon: ImageVector, val iconColor: Color, val title: String) {
    object Chronology : DrawerItem("chronology", Icons.Default.History, Color.Gray,"CHRONOLOGY")
    object Articles : DrawerItem("articles", Icons.Default.Article, Blue500, "ARTICLES")
    object Videos : DrawerItem("videos", Icons.Default.VideoLibrary, VideoRed, "VIDEOS")
    object Settings : DrawerItem("settings", Icons.Default.Settings, Color.DarkGray,"SETTINGS")
}

