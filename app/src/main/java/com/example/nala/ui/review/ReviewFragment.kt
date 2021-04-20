package com.example.nala.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.ui.composables.ReviewListScreen
import com.example.nala.ui.dictionary.DictionaryViewModel
import com.example.nala.ui.theme.AppTheme

class ReviewFragment : Fragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()

    private val reviewViewModel: ReviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reviewViewModel.loadWordReviewItems()
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = false) {
                    ReviewListScreen(
                        isLoading = reviewViewModel.reviewsLoading.value,
                        selectedCategory = reviewViewModel.selectedCategory.value,
                        setCategory = reviewViewModel::setCategory,
                        wordReviewItems = reviewViewModel.wordReviewItems.value,
                        sentenceReviewItems = reviewViewModel.sentenceReviewItems.value,
                        kanjiReviewItems = reviewViewModel.kanjiReviewItems.value,
                        loadWordReviews = reviewViewModel::loadWordReviewItems,
                        loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
                        loadKanjiReviews =reviewViewModel::loadKaniReviewItems,
                        setWordItem = viewModel::setCurrentWordFromReview,
                        setSentenceItem = viewModel::setCurrentSentenceFromReview,
                        setKanjiItem = viewModel::setCurrentKanji,
                        removeWordReview = reviewViewModel::removeWordReviewItem,
                        removeSentenceReview = reviewViewModel::removeSentenceReviewItem,
                        removeKanjiReview = reviewViewModel::removeKanjiReviewItem,
                        dismissWordReview = reviewViewModel::dismissWordReviewItem,
                        dismissSentenceReview = reviewViewModel::dismissSentenceReviewItem,
                        dismissKanjiReview = reviewViewModel::dismissKanjiReviewItem,
                        isHomeSelected = viewModel.isHomeSelected.value,
                        isReviewsSelected = viewModel.isReviewSelected.value,
                        toggleHome = viewModel::toggleHome,
                        toggleReviews = viewModel::toggleReviews,
                        updateWordReviewItem = reviewViewModel::updateWordReviewItem,
                        updateSentenceReviewItem= reviewViewModel::updateSentenceReviewItem,
                        updateKanjiReviewItem = reviewViewModel::updateKanjiReviewItem,
                        navController = findNavController())
                }
            }
        }
    }
}