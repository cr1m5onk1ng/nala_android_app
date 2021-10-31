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
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.utils.extensions.isLastVisibleItem

@Composable
fun SentenceReviewsScreen(
    sentenceReviewItems: DataState<List<SentenceReviewModel>>,
    sentencesEndReached: Boolean,
    sentencesListState: LazyListState,
    setSentenceItem: (String) -> Unit,
    setTargetWordItem: (String) -> Unit,
    removeSentenceReview: (SentenceReviewModel) -> Unit,
    removedSentenceReview: MutableState<SentenceReviewModel?>,
    updateSentenceReviewItem: (quality: Int, sentenceReview: SentenceReviewModel) -> Unit,
    onUpdateSentenceReviews: () -> Unit,
    onShare: (String?) -> Unit,
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
        when(sentenceReviewItems){
            is DataState.Initial<*> -> {
                LoadingIndicator()
            }
            is DataState.Error -> {
                reviewsLoading.value = false
                ErrorScreen(
                    text = stringResource(R.string.no_sentence_in_review),
                    subtitle = ""
                )
            }
            is DataState.Loading -> {
                reviewsLoading.value = true
            }
            is DataState.Success<List<SentenceReviewModel>>  -> {
                reviewsLoading.value = false
                val items = sentenceReviewItems.data
                LazyColumn(state = sentencesListState) {
                    items(count = items.size) { index ->
                        SentenceReviewCard(
                            items[index],
                            removedSentenceReview,
                            setSentenceItem,
                            setTargetWordItem,
                            updateSentenceReviewItem,
                            removeSentenceReview,
                            onShare,
                            scaffoldState,
                            showSnackbar,
                            navController
                        )
                    }
                    if(reviewsLoading.value) {
                        item{ LoadingIndicator() }
                    }
                    /*
                    if(sentencesListState.isLastVisibleItem(items.size - 1) &&
                        sentencesListState.isScrollInProgress && (!sentencesEndReached)
                    ) {
                        onUpdateSentenceReviews()
                    } */
                }
            }
        }
    }
}