package com.example.nala.network.model.menus

import androidx.compose.ui.graphics.vector.ImageVector

data class ActionModel(
    val icon: ImageVector,
    val action: () -> Unit,
    val isActive: Boolean,
)
