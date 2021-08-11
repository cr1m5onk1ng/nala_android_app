package com.example.nala.ui.composables

import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun CustomTabMenu(tabIndex: Int, setTabIndex: (Int) -> Unit, tabHeaders: List<String>) {
    TabRow(selectedTabIndex = tabIndex,backgroundColor = Color.Transparent) {
        tabHeaders.forEachIndexed { index, text ->
            Tab(selected = tabIndex == index, onClick = {
                setTabIndex(index)
            }, text = {
                Text(text = text)
            })
        }
    }
}