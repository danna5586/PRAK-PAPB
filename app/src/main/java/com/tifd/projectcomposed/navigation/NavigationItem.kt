package com.tifd.projectcomposed.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val tittle: String,
    val icon: ImageVector,
    val screen: Screen
)
