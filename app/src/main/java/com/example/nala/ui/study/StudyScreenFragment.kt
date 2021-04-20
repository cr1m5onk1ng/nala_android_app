package com.example.nala.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.nala.ui.composables.StudyScreen
import com.example.nala.ui.dictionary.DictionaryViewModel

class StudyScreenFragment : Fragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()
    private val studyViewModel: StudyViewModel by activityViewModels()
    private val sentence = "昨日の夜遅く テレビで\n" +
            "やっていた映画を見たんだ\n" +
            "未来の世界を舞台にした\n" +
            "海外の古いSF"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply{
            setContent {
                StudyScreen(
                    context = studyViewModel.currentStudyContext.value ?: "",
                    wordModel = studyViewModel.currentStudyTargetWord.value,
                    kanjiDict = viewModel.kanjiDict,
                    navController = findNavController(),
                    setCurrentKanji = viewModel::setCurrentKanji,
                    setCurrentStory = viewModel::setCurrentStory,
                    contextLoading = studyViewModel.contextLoading.value,
                    wordLoading = studyViewModel.wordModelLoading.value,
                    addSentenceToReview = viewModel::addSentenceToReview
                )
            }
        }
    }
}