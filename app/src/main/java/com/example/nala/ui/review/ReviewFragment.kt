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
import com.example.nala.ui.CustomFragment
import com.example.nala.ui.composables.review.ReviewListScreen
import com.example.nala.ui.dictionary.DictionaryViewModel
import com.example.nala.ui.study.StudyViewModel
import com.example.nala.ui.theme.AppTheme

class ReviewFragment : CustomFragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()
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
                    ReviewListScreen(
                        selectedCategory = reviewViewModel.selectedCategory.value,
                        setCategory = reviewViewModel::setCategory,
                        wordReviewItems = reviewViewModel.wordReviewItemsState.collectAsState().value,
                        sentenceReviewItems = reviewViewModel.sentenceReviewItemsState.collectAsState().value,
                        kanjiReviewItems = reviewViewModel.kanjiReviewItemsState.collectAsState().value,
                        wordsEndReached = reviewViewModel.wordsEndReached.value,
                        setWordItem = viewModel::setCurrentWordFromReview,
                        setSentenceItem = studyViewModel::setStudyContext,
                        setTargetWordItem = studyViewModel::setStudyTargetWord,
                        setKanjiItem = viewModel::setCurrentKanji,
                        removeWordReview = reviewViewModel::removeWordReviewItem,
                        removeSentenceReview = reviewViewModel::removeSentenceReviewItem,
                        removeKanjiReview = reviewViewModel::removeKanjiReviewItem,
                        addWordToReview = reviewViewModel::restoreWordFromReview,
                        addSentenceToReview =reviewViewModel::restoreSentenceFromReview,
                        addKanjiToReview = reviewViewModel::restoreKanjiFromReview,
                        isHomeSelected = viewModel.isHomeSelected.value,
                        isReviewsSelected = viewModel.isReviewSelected.value,
                        toggleHome = viewModel::toggleHome,
                        toggleReviews = viewModel::toggleReviews,
                        updateWordReviewItem = reviewViewModel::updateWordReviewItem,
                        updateSentenceReviewItem= reviewViewModel::updateSentenceReviewItem,
                        updateKanjiReviewItem = reviewViewModel::updateKanjiReviewItem,
                        navController = findNavController(),
                        onShare = {},
                        onSearch = reviewViewModel::searchFlow,
                        onRestore = reviewViewModel::restore,
                        onUpdateWordReviews = {},
                        showSnackbar = {
                            showSnackbar(
                                scaffoldState,
                                message="Review removed",
                                actionLabel="Undo",
                            )},
                        scaffoldState = scaffoldState,
                    )
                }
            }
        }
    }
}