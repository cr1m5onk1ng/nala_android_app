package com.example.nala.utils.extensions

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isLastVisibleItem(index: Int) =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == index

fun LazyListState.isItemVisible(index: Int): Boolean {
    val firstVisible = layoutInfo.visibleItemsInfo.firstOrNull()?.index
    val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index
    if(firstVisible != null && lastVisible != null) {
        return index >= firstVisible && index < lastVisible
    }
    return false
}