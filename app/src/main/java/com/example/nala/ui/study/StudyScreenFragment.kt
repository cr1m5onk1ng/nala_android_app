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
import com.example.nala.ui.composables.study.StudyScreen
import com.example.nala.ui.dictionary.DictionaryViewModel
import com.example.nala.ui.review.ReviewViewModel

class StudyScreenFragment : CustomFragment() {

    private val viewModel: DictionaryViewModel by activityViewModels()
    private val studyViewModel: StudyViewModel by activityViewModels()
    private val reviewViewModel: ReviewViewModel by activityViewModels()
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
                val scaffoldState = rememberScaffoldState()
                StudyScreen(
                    studyContextState = studyViewModel.studyContextState.value,
                    targetWordState = studyViewModel.targetWordState.value,
                    similarSentencesState = studyViewModel.similarSentencesState.value,
                    navController = findNavController(),
                    setSharedSentence = viewModel::setSharedSentence,
                    setCurrentWord = viewModel::setCurrentWordFromStudy,
                    unsetTargetWord = studyViewModel::unsetSelectedWord,
                    addSentenceToReview = viewModel::addSentenceToReview,
                    loadSimilarSentences = studyViewModel::loadSimilarSentences,
                    setIsWordFromForm = viewModel::setIsWordFromForm,
                    scaffoldState = scaffoldState,
                    showReviewSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                    showSaveSnackbar = {showSnackbar(scaffoldState, message="Sentence added to corpus")}
                )
            }
        }
    }
}