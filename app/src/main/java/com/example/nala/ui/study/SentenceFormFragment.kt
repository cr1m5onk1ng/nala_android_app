package com.example.nala.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.ui.CustomFragment
import com.example.nala.ui.composables.OneTargetForm
import com.example.nala.ui.composables.StudyScreen
import com.example.nala.ui.dictionary.DictionaryViewModel

class SentenceFormFragment : CustomFragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()
    private val studyViewModel: StudyViewModel by activityViewModels()

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
                    sentenceReceived = viewModel.sentenceReceived.value,
                    selectedWord = studyViewModel.selectedWord.value,
                    onSentenceAdd = studyViewModel::setStudyContext,
                    onWordAdd = studyViewModel::setStudyTargetWord,
                    onWordSelect = studyViewModel::setSelectedWord,
                    unsetSharedSentence = viewModel::unsetSharedSentence,
                    addSentenceToReview = viewModel::addSentenceToReview,
                    showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                    navController = findNavController()
                )
            }
        }
    }

}
