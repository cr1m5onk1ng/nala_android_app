package com.example.nala.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.ui.CustomFragment
import com.example.nala.ui.composables.dictionary.KanjiDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KanjiDetailFragment : CustomFragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply{
            setContent {
                val scaffoldState = rememberScaffoldState()
                KanjiDetailScreen(
                    kanjiSearchState = viewModel.kanjiSearchState.value,
                    kanjiStoryState = viewModel.kanjiStoryState.value,
                    storyFormActive = viewModel.editStoryFormActive.value,
                    addKanjiToReview = viewModel::addKanjiToReview,
                    updateKanjiStory = viewModel::updateKanjiStory,
                    setCurrentStory = viewModel::setCurrentStory,
                    toggleStoryEditForm = viewModel::toggleEditStoryForm,
                    navController = findNavController(),
                    scaffoldState = scaffoldState,
                    onShare = {},
                    showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                )
            }
        }
    }

}