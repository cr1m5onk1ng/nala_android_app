package com.example.nala.ui.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.nala.ui.CustomFragment
import com.example.nala.ui.composables.DictionaryDetailScreen
import com.example.nala.ui.review.ReviewViewModel
import com.example.nala.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DictionaryDetailFragment : CustomFragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()

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
                        viewModel.currentWordModel.value,
                        isLoading = viewModel.searchLoading.value,
                        navController = findNavController(),
                        kanjiDict = viewModel.kanjiDict,
                        setCurrentKanji = viewModel::setCurrentKanji,
                        setCurrentStory = viewModel::setCurrentStory,
                        addToReview =  viewModel::addWordToReview,
                        scaffoldState = scaffoldState,
                        onShowSnackbar = {showSnackbar(scaffoldState, message="Added to review")}
                    )
                }
            }
        }
    }
}