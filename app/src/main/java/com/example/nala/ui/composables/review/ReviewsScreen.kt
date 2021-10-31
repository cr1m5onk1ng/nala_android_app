package com.example.nala.ui.composables.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.db.models.review.KanjiReviewCache
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.*
import com.example.nala.ui.composables.menus.NalaBottomBar
import com.example.nala.ui.composables.menus.ReviewScreensTab
import com.example.nala.ui.composables.menus.ReviewsTopBar
import com.example.nala.ui.theme.Blue900

@ExperimentalComposeUiApi
@Composable
fun ReviewsScreen(
    selectedTab: Int,
    setSelectedTab: (Int) -> Unit,
    wordReviewItems: DataState<List<WordReviewModel>>,
    sentenceReviewItems: DataState<List<SentenceReviewModel>>,
    kanjiReviewItems: DataState<List<KanjiReviewCache>>,
    wordsEndReached: Boolean,
    sentencesEndReached: Boolean,
    kanjisEndReached: Boolean,
    wordsListState: LazyListState,
    sentencesListState: LazyListState,
    kanjisListState: LazyListState,
    setWordItem: (WordReviewModel) -> Unit,
    setSentenceItem: (String) -> Unit,
    setTargetWordItem: (String) -> Unit,
    setKanjiItem: (String) -> Unit,
    removeWordReview: (WordReviewModel) -> Unit,
    removeSentenceReview: (SentenceReviewModel) -> Unit,
    removeKanjiReview: (KanjiReviewCache) -> Unit,
    addWordToReview: (WordReviewModel) -> Unit,
    addSentenceToReview: (SentenceReviewModel) -> Unit,
    addKanjiToReview: (KanjiReviewCache) -> Unit,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
    updateWordReviewItem: (quality: Int, reviewItem: WordReviewModel) -> Unit,
    updateSentenceReviewItem: (quality: Int, sentenceReview: SentenceReviewModel) -> Unit,
    updateKanjiReviewItem: (quality: Int, kanjiReview: KanjiReviewCache) -> Unit,
    onSearch: (String) -> Unit,
    onRestore: () -> Unit,
    onShare: (String?) -> Unit,
    onUpdateWordReviews: () -> Unit,
    onUpdateSentenceReviews: () -> Unit,
    onUpdateKanjiReviews: () -> Unit,
    navController: NavController,
    scaffoldState: ScaffoldState,
    showSnackbar: (ScaffoldState) -> Unit
) {
    val searchOpen = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val removedWordReview = remember { mutableStateOf<WordReviewModel?>(null) }
    val removedSentenceReview = remember { mutableStateOf<SentenceReviewModel?>(null) }
    val removedKanjiReview = remember { mutableStateOf<KanjiReviewCache?>(null) }


    Scaffold(
        topBar = {
            /*
            ReviewsTopBar(
                searchOpen = searchOpen,
                searchQuery = searchQuery,
                onSearch = onSearch,
                onRestore = onRestore,
                navController = navController,
            )*/

            TopAppBar(
                elevation = 0.dp,
                title = {

                    Text(
                        text = stringResource(R.string.reviews_screen_title),
                        style = MaterialTheme.typography.h6
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    BackButton(iconColor = Color.White, navController = navController)
                },
                actions = {
                    if(!searchOpen.value)
                        IconButton(onClick = {
                            searchOpen.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                            )
                        }
                }
            )
        },
        bottomBar = {
            NalaBottomBar(navController = navController)
        }
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier.padding(innerPadding)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(searchOpen.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(Dp.Hairline, Blue900, RectangleShape)
                            .background(MaterialTheme.colors.primary),
                            //.background(MaterialTheme.colors.primary),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        SearchField(
                            searchQuery = searchQuery,
                            searchOpen = searchOpen,
                            onSearch = onSearch,
                            onRestore = onRestore,
                        )
                    }
                }
                ReviewScreensTab(tabIndex = selectedTab, setTabIndex = { setSelectedTab(it) })
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                ) {
                    when(selectedTab) {
                        0 -> {
                           WordReviewsScreen(
                               wordReviewItems = wordReviewItems,
                               wordsEndReached = wordsEndReached,
                               wordsListState = wordsListState,
                               setWordItem = setWordItem,
                               removeWordReview = removeWordReview,
                               removedWordReview = removedWordReview,
                               updateWordReviewItem = updateWordReviewItem,
                               onShare = onShare,
                               onUpdateWordReviews = onUpdateWordReviews,
                               navController = navController,
                               scaffoldState = scaffoldState,
                               showSnackbar = showSnackbar,
                           )
                        }
                        1 -> {
                            SentenceReviewsScreen(
                                sentenceReviewItems = sentenceReviewItems,
                                sentencesEndReached = sentencesEndReached,
                                sentencesListState = sentencesListState,
                                setSentenceItem = setSentenceItem,
                                setTargetWordItem = setTargetWordItem,
                                removeSentenceReview = removeSentenceReview,
                                removedSentenceReview = removedSentenceReview,
                                updateSentenceReviewItem = updateSentenceReviewItem,
                                onUpdateSentenceReviews = onUpdateSentenceReviews,
                                onShare = onShare,
                                navController = navController,
                                scaffoldState = scaffoldState,
                                showSnackbar =showSnackbar
                            )
                        }
                        2 -> {
                            KanjiReviewsScreen(
                                kanjiReviewItems = kanjiReviewItems,
                                kanjisEndReached = kanjisEndReached,
                                kanjisListState = kanjisListState,
                                setKanjiItem = setKanjiItem,
                                removeKanjiReview = removeKanjiReview,
                                removedKanjiReview = removedKanjiReview,
                                updateKanjiReviewItem = updateKanjiReviewItem,
                                onShare = onShare,
                                onUpdateKanjiReviews = onUpdateKanjiReviews,
                                navController = navController,
                                scaffoldState = scaffoldState,
                                showSnackbar = showSnackbar,
                            )
                        }
                    }
                }
            }
            val snackbar = createRef()
            DefaultSnackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(snackbar) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                snackbarHostState = scaffoldState.snackbarHostState,
                onDismiss = {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                },
                onAction = {
                    when(selectedTab){
                        0 -> {
                            removedWordReview.value?.let{
                                addWordToReview(it)
                            }
                        }
                        1 -> {
                            removedSentenceReview.value?.let{
                                addSentenceToReview(it)
                            }
                        }
                        2 -> {
                            removedKanjiReview.value?.let{
                                addKanjiToReview(it)
                            }
                        }
                    }
                }
            )
        }
    }
}