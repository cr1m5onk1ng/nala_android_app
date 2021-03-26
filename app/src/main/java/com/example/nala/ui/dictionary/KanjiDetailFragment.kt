package com.example.nala.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.ui.composables.KanjiDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KanjiDetailFragment : Fragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply{
            setContent {
                KanjiDetailScreen(
                    kanji = viewModel.currentKanji.value,
                    story = viewModel.currentStory.value,
                    navController = findNavController(),
                )
            }
        }
    }

}