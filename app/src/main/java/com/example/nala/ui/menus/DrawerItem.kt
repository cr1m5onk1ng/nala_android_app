package com.example.nala.ui.menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector


sealed class DrawerItem(val route: String, val icon: ImageVector, val title: String) {
    object Chronology : DrawerItem("chronology", Icons.Default.History, "CHRONOLOGY")
    object Articles : DrawerItem("articles", Icons.Default.Article, "ARTICLES")
    object Videos : DrawerItem("videos", Icons.Default.VideoLibrary, "VIDEOS")
    object Setting : DrawerItem("setting", Icons.Default.Settings, "SETTINGS")
}

