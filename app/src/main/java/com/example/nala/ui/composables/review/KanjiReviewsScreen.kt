package com.example.nala.ui.composables.review

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.db.models.review.KanjiReviewCache
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.utils.extensions.isLastVisibleItem

@Composable
fun KanjiReviewsScreen(
    kanjiReviewItems: DataState<List<KanjiReviewCache>>,
    kanjisEndReached: Boolean,
    kanjisListState: LazyListState,
    setKanjiItem: (String) -> Unit,
    removeKanjiReview: (KanjiReviewCache) -> Unit,
    removedKanjiReview: MutableState<KanjiReviewCache?>,
    updateKanjiReviewItem: (quality: Int, kanjiReview: KanjiReviewCache) -> Unit,
    onShare: (String?) -> Unit,
    onUpdateKanjiReviews: () -> Unit,
    navController: NavController,
    scaffoldState: ScaffoldState,
    showSnackbar: (ScaffoldState) -> Unit
) {
    val reviewsLoading = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxHeight()
    ) {
        when(kanjiReviewItems){
            is DataState.Initial<*> -> {
                LoadingIndicator()
            }
            is DataState.Error -> {
                reviewsLoading.value = false
                ErrorScreen(
                    text = stringResource(R.string.no_kanjis_in_review),
                    subtitle = ""
                )
            }
            is DataState.Loading -> {
                reviewsLoading.value = true
            }
            is DataState.Success<List<KanjiReviewCache>>  -> {
                reviewsLoading.value = false
                val items = kanjiReviewItems.data
                LazyColumn(state = kanjisListState) {
                    items(count = items.size) { index ->
                        KanjiReviewCard(
                            items[index],
                            removedKanjiReview,
                            setKanjiItem,
                            updateKanjiReviewItem,
                            removeKanjiReview,
                            onShare,
                            scaffoldState,
                            showSnackbar,
                            navController
                        )
                    }
                    if(reviewsLoading.value){
                        item { LoadingIndicator() }
                    }
                    /*
                    if(kanjisListState.isLastVisibleItem(items.size - 1) &&
                        kanjisListState.isScrollInProgress && (!kanjisEndReached)
                    ) {
                        onUpdateKanjiReviews()
                    }*/
                }
            }
        }
    }
}