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
import com.example.nala.ui.composables.StudyScreen
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
                    context = studyViewModel.currentStudyContext.value ?: "",
                    wordModel = studyViewModel.currentStudyTargetWord.value,
                    similarSentences = studyViewModel.similarSentences.value,
                    wordKanjis = viewModel.currentWordKanjis.value,
                    navController = findNavController(),
                    setCurrentKanji = viewModel::setCurrentKanji,
                    setCurrentStory = viewModel::setCurrentStory,
                    setSharedSentence = viewModel::setSharedSentence,
                    unsetTargetWord = studyViewModel::unsetSelectedWord,
                    contextLoading = studyViewModel.contextLoading.value,
                    wordLoading = studyViewModel.wordModelLoading.value,
                    sentencesLoading = studyViewModel.similarSentencesLoading.value,
                    kanjisLoading = studyViewModel.kanjisLoading.value,
                    addSentenceToReview = viewModel::addSentenceToReview,
                    loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
                    loadSimilarSentences = studyViewModel::loadSimilarSentences,
                    scaffoldState = scaffoldState,
                    showReviewSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                    showSaveSnackbar = {showSnackbar(scaffoldState, message="Sentence added to corpus")}
                )
            }
        }
    }
}