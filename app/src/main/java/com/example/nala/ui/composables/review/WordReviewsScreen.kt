package com.example.nala.ui.composables.review

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.CircularProgressIndicator
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
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.utils.extensions.isLastVisibleItem

@Composable
fun WordReviewsScreen(
    wordReviewItems: DataState<List<WordReviewModel>>,
    wordsEndReached: Boolean,
    wordsListState: LazyListState,
    setWordItem: (WordReviewModel) -> Unit,
    removeWordReview: (WordReviewModel) -> Unit,
    removedWordReview: MutableState<WordReviewModel?>,
    updateWordReviewItem: (quality: Int, reviewItem: WordReviewModel) -> Unit,
    onShare: (String?) -> Unit,
    onUpdateWordReviews: () -> Unit,
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
        when(wordReviewItems){
            is DataState.Initial<*> -> {
                LoadingIndicator()
            }
            is DataState.Error -> {
                reviewsLoading.value = false
                ErrorScreen(
                    text = stringResource(R.string.no_words_in_review),
                    subtitle = ""
                )
            }
            is DataState.Success<List<WordReviewModel>>  -> {
                reviewsLoading.value = false
                val items = wordReviewItems.data
                LazyColumn(state = wordsListState) {
                    items(count = items.size) { index ->
                        WordReviewCard(
                            items[index],
                            removedWordReview,
                            setWordItem,
                            updateWordReviewItem,
                            removeWordReview,
                            onShare,
                            scaffoldState,
                            showSnackbar,
                            navController,
                        )

                    }
                    if(reviewsLoading.value) {
                        item{ CircularProgressIndicator() }
                    }
                    /*
                    if(wordsListState.isLastVisibleItem(items.size - 1) &&
                        wordsListState.isScrollInProgress && (!wordsEndReached)
                    ) {
                        onUpdateWordReviews()
                    }*/
                }
            }
            is DataState.Loading -> {
                //LoadingIndicator()
                reviewsLoading.value = true
            }
        }
    }
}