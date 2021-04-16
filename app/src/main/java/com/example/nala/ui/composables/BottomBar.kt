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
import com.example.nala.R


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
                    navController.popBackStack()
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
                    navController.navigate(R.id.show_reviews)
                } },
            icon = {Icon(
                Icons.Rounded.List,
                contentDescription="home")
            }
        )
    }
}
