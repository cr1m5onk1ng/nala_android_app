package com.example.nala.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.R
import com.example.nala.ui.CustomFragment
import com.example.nala.ui.composables.review.ReviewListScreen
import com.example.nala.ui.composables.review.ReviewsScreen
import com.example.nala.ui.dictionary.DictionaryViewModel
import com.example.nala.ui.study.StudyViewModel
import com.example.nala.ui.theme.AppTheme

class ReviewFragment : CustomFragment() {

    private val dictViewModel: DictionaryViewModel by activityViewModels()
    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private val studyViewModel: StudyViewModel by activityViewModels()

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val scaffoldState = rememberScaffoldState()
                AppTheme(darkTheme = false) {
                    ReviewsScreen(
                        selectedTab = reviewViewModel.selectedTab.value,
                        setSelectedTab = reviewViewModel::setTab,
                        wordReviewItems = reviewViewModel.wordReviewItemsState.collectAsState().value,
                        sentenceReviewItems = reviewViewModel.sentenceReviewItemsState.collectAsState().value,
                        kanjiReviewItems = reviewViewModel.kanjiReviewItemsState.collectAsState().value,
                        wordsEndReached = reviewViewModel.wordsEndReached.value,
                        sentencesEndReached = reviewViewModel.sentencesEndReached.value,
                        kanjisEndReached = reviewViewModel.kanjisEndReached.value,
                        wordsListState = reviewViewModel.wordsListState,
                        sentencesListState = reviewViewModel.sentencesListState,
                        kanjisListState = reviewViewModel.kanjisListState,
                        setWordItem = dictViewModel::setCurrentWordFromReview,
                        setSentenceItem = studyViewModel::setStudyContext,
                        setTargetWordItem = studyViewModel::setStudyTargetWord,
                        setKanjiItem = dictViewModel::setCurrentKanji,
                        removeWordReview = reviewViewModel::removeWordReviewItem,
                        removeSentenceReview = reviewViewModel::removeSentenceReviewItem,
                        removeKanjiReview = reviewViewModel::removeKanjiReviewItem,
                        addWordToReview = reviewViewModel::restoreWordFromReview,
                        addSentenceToReview =reviewViewModel::restoreSentenceFromReview,
                        addKanjiToReview = reviewViewModel::restoreKanjiFromReview,
                        isHomeSelected = dictViewModel.isHomeSelected.value,
                        isReviewsSelected = dictViewModel.isReviewSelected.value,
                        toggleHome = dictViewModel::toggleHome,
                        toggleReviews = dictViewModel::toggleReviews,
                        updateWordReviewItem = reviewViewModel::updateWordReviewItem,
                        updateSentenceReviewItem= reviewViewModel::updateSentenceReviewItem,
                        updateKanjiReviewItem = reviewViewModel::updateKanjiReviewItem,
                        onShare = {},
                        onSearch = reviewViewModel::searchFlow,
                        onRestore = reviewViewModel::restore,
                        onUpdateWordReviews = reviewViewModel::loadPagedWordReviewItems,
                        onUpdateSentenceReviews = reviewViewModel::loadPagedSentenceReviewItems,
                        onUpdateKanjiReviews = reviewViewModel::loadPagedKanjiReviewItems,
                        navController = findNavController(),
                        showSnackbar = {
                            showSnackbar(
                                scaffoldState,
                                message = getString(R.string.review_removed),
                                actionLabel="UNDO",
                            )},
                        scaffoldState = scaffoldState,
                    )
                }
            }
        }
    }
}