package com.example.nala.ui.composables.menus

import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.nala.R

@Composable
fun ReviewScreensTab(tabIndex: Int, setTabIndex: (Int) -> Unit) {
    val tabData = listOf(
        stringResource(R.string.word_reviews_title),
        stringResource(R.string.sentence_reviews_title),
        stringResource(R.string.kanji_reviews_title),
    )
    TabRow(selectedTabIndex = tabIndex,backgroundColor = Color.Transparent) {
        tabData.forEachIndexed { index, text ->
            Tab(selected = tabIndex == index, onClick = {
                setTabIndex(index)
            }, text = {
                Text(text = text)
            })
        }
    }
}