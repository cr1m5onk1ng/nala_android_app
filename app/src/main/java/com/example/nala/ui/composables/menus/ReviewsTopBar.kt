package com.example.nala.ui.composables.menus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.ui.composables.BackButton
import com.example.nala.ui.composables.review.SearchField

@ExperimentalComposeUiApi
@Composable
fun ReviewsTopBar(
    searchOpen: MutableState<Boolean>,
    searchQuery: MutableState<String>,
    onSearch: (String) -> Unit,
    onRestore: () -> Unit,
    navController: NavController,
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.primary,),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // TITLE ROW
        if(!searchOpen.value) {
            Row() {
                BackButton(iconColor = Color.White, navController = navController)
                Text(
                    text = stringResource(R.string.reviews_screen_title),
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                )
            }
        }
        // SEARCH FIELD ROW
        Row() {
            if(!searchOpen.value)
                IconButton(onClick = { searchOpen.value = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                    )
                }
            else
                SearchField(
                    searchQuery = searchQuery,
                    searchOpen = searchOpen,
                    onSearch = onSearch,
                    onRestore = onRestore,
                )
        }
    }
}