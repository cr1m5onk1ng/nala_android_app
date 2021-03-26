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
import com.example.nala.ui.dictionary.DictionaryEvent
import com.example.nala.ui.dictionary.DictionaryViewModel
import com.example.nala.ui.theme.AppTheme

class ReviewFragment : Fragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.onTriggerEvent(DictionaryEvent.LoadReviewsEvent)
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = false) {
                    ReviewListScreen(
                        isLoading = viewModel.reviewsLoading.value,
                        reviewItems = viewModel.reviewItems.value ,
                        setStudyItem = viewModel::setCurrentWord,
                        isHomeSelected = viewModel.isHomeSelected.value,
                        isReviewsSelected = viewModel.isReviewSelected.value,
                        toggleHome = viewModel::toggleHome,
                        toggleReviews = viewModel::toggleReviews,
                        navController = findNavController())
                }
            }
        }
    }
}