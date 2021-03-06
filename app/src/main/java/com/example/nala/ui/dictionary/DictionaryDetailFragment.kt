package com.example.nala.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.ui.CustomFragment
import com.example.nala.ui.composables.dictionary.DictionaryDetailScreen
import com.example.nala.ui.review.ReviewViewModel
import com.example.nala.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DictionaryDetailFragment : CustomFragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()
    private val reviewViewModel: ReviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply{
            setContent {
                val scaffoldState = rememberScaffoldState()
                AppTheme(darkTheme = false) {
                    DictionaryDetailScreen(
                        searchState = viewModel.wordSearchState.value,
                        onRetry = viewModel::retrySearch,
                        navController = findNavController(),
                        wordKanjis = viewModel.currentWordKanjis.value,
                        setCurrentKanji = viewModel::setCurrentKanji,
                        setCurrentStory = viewModel::setCurrentStory,
                        addToReview =  viewModel::addWordToReview,
                        removeFromReview = {},
                        scaffoldState = scaffoldState,
                        onShare = {},
                        showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                    )
                }
            }
        }
    }
}