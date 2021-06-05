package com.example.nala.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.ui.CustomFragment
import com.example.nala.ui.composables.OneTargetForm
import com.example.nala.ui.dictionary.DictionaryViewModel
import com.example.nala.ui.review.ReviewViewModel

class SentenceFormFragment : CustomFragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()
    private val studyViewModel: StudyViewModel by activityViewModels()
    private val reviewViewModel: ReviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply{
            setContent {
                val scaffoldState = rememberScaffoldState()
                OneTargetForm(
                    sentence = viewModel.sharedSentence.value,
                    tokens = viewModel.sharedSentenceTokens.value,
                    tokensIndexMap = viewModel.sharedSentenceTokensIndexMap.value,
                    sentenceLoading = viewModel.sentenceReceived.value,
                    selectedWord = studyViewModel.selectedWord.value,
                    onSentenceAdd = studyViewModel::setStudyContext,
                    setKanjis = studyViewModel::setCurrentWordKanjis,
                    onWordAdd = studyViewModel::setStudyTargetWord,
                    onWordSelect = studyViewModel::setSelectedWord,
                    unsetSharedSentence = viewModel::unsetSharedSentence,
                    unsetSelectedWord = studyViewModel::unsetSelectedWord,
                    addSentenceToReview = viewModel::addSentenceToReview,
                    loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
                    scaffoldState = scaffoldState,
                    showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                    navController = findNavController()
                )
            }
        }
    }

}
