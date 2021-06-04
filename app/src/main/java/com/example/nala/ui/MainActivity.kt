package com.example.nala.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nala.ui.composables.*
import com.example.nala.ui.dictionary.DictionaryEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.nala.ui.dictionary.DictionaryViewModel
import com.example.nala.ui.review.ReviewViewModel
import com.example.nala.ui.study.StudyViewModel
import com.example.nala.ui.theme.AppTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: BaseApplication

    private val viewModel: DictionaryViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val studyViewModel: StudyViewModel by viewModels()

    @ExperimentalComposeUiApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)){
                    viewModel.setSharedText(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) )
                }
            }
            Intent.ACTION_SEND -> {
                Log.d("SHARED", "ACTION SEND CALLED!")
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        Log.d("SHARED", "SHARED TEXT: $it")
                        viewModel.setSharedSentence(it)
                    }
                } else {
                    Log.d("SHARED", "DIDNT PROCESS TEXT!")
                }
            }
            else -> {
                // DONT BOTHER
            }
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContent{
            AppTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()

                // Navigate to detail screen if a word was searched from another app
                val startDestination = "home_screen"/* if(viewModel.textReceived.value) {
                    "detail_screen"
                } else if(viewModel.sentenceReceived.value) {
                    "sentence_form_screen"
                } else{
                    "home_screen"
                } */

                NavHost(navController=navController, startDestination) {
                    composable("home_screen"){
                        HomeScreen(
                            query = viewModel.query.value,
                            mightForgetItems = viewModel.mightForgetItems.value,
                            mightForgetItemsLoaded = viewModel.mightForgetItemsLoaded.value,
                            onQueryChange = viewModel::onQueryChanged,
                            onClick = {viewModel.onTriggerEvent(DictionaryEvent.SearchWordEvent)},
                            textReceived = viewModel.textReceived.value,
                            sentenceReceived = viewModel.sentenceReceived.value,
                            isHomeSelected = viewModel.isHomeSelected.value,
                            isReviewsSelected = viewModel.isReviewSelected.value,
                            toggleHome = viewModel::toggleHome,
                            toggleReviews = viewModel::toggleReviews,
                            navController = navController
                        )
                    }

                    composable("detail_screen") {
                        DictionaryDetailScreen(
                            viewModel.currentWordModel.value,
                            isLoading = viewModel.searchLoading.value,
                            navController = navController,
                            wordKanjis = viewModel.currentWordKanjis.value,
                            setCurrentKanji = viewModel::setCurrentKanji,
                            setCurrentStory = viewModel::setCurrentStory,
                            unsetSharedWord = viewModel::unsetSharedText,
                            addToReview =  viewModel::addWordToReview,
                            loadWordReviews = reviewViewModel::loadWordReviewItems,
                            scaffoldState = scaffoldState,
                            showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                        )
                    }

                    composable("kanji_detail_screen"){
                        KanjiDetailScreen(
                            kanji = viewModel.currentKanji.value,
                            story = viewModel.currentStory.value,
                            kanjiSet = viewModel.kanjiSet.value,
                            storySet = viewModel.storySet.value,
                            storyFormActive = viewModel.editStoryFormActive.value,
                            addKanjiToReview = viewModel::addKanjiToReview,
                            updateKanjiStory = viewModel::updateKanjiStory,
                            setCurrentStory = viewModel::setCurrentStory,
                            toggleStoryEditForm = viewModel::toggleEditStoryForm,
                            navController = navController,
                            scaffoldState = scaffoldState,
                            showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                        )
                    }

                    composable("review_screen") {
                        ReviewListScreen(
                            isLoading = reviewViewModel.reviewsLoading.value,
                            selectedCategory = reviewViewModel.selectedCategory.value,
                            setCategory = reviewViewModel::setCategory,
                            wordReviewItems = reviewViewModel.wordReviewItems.value,
                            sentenceReviewItems = reviewViewModel.sentenceReviewItems.value,
                            kanjiReviewItems = reviewViewModel.kanjiReviewItems.value,
                            loadWordReviews = reviewViewModel::loadWordReviewItems,
                            loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
                            loadKanjiReviews =reviewViewModel::loadKanjiReviewItems,
                            setWordItem = viewModel::setCurrentWordFromReview,
                            setSentenceItem = studyViewModel::setStudyContext,
                            setTargetWordItem = studyViewModel::setStudyTargetWord,
                            setKanjiItem = viewModel::setCurrentKanji,
                            removeWordReview = reviewViewModel::removeWordReviewItem,
                            removeSentenceReview = reviewViewModel::removeSentenceReviewItem,
                            removeKanjiReview = reviewViewModel::removeKanjiReviewItem,
                            dismissWordReview = reviewViewModel::dismissWordReviewItem,
                            dismissSentenceReview = reviewViewModel::dismissSentenceReviewItem,
                            dismissKanjiReview = reviewViewModel::dismissKanjiReviewItem,
                            isHomeSelected = viewModel.isHomeSelected.value,
                            isReviewsSelected = viewModel.isReviewSelected.value,
                            toggleHome = viewModel::toggleHome,
                            toggleReviews = viewModel::toggleReviews,
                            updateWordReviewItem = reviewViewModel::updateWordReviewItem,
                            updateSentenceReviewItem= reviewViewModel::updateSentenceReviewItem,
                            updateKanjiReviewItem = reviewViewModel::updateKanjiReviewItem,
                            navController = navController,
                            showSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message="Review removed",
                                    actionLabel="Undo",
                                )},
                            scaffoldState = scaffoldState,
                        )

                    }

                    composable("sentence_form_screen"){
                        OneTargetForm(
                            sentence = viewModel.sharedSentence.value,
                            tokens = viewModel.sharedSentenceTokens.value,
                            tokensIndexMap = viewModel.sharedSentenceTokensIndexMap.value,
                            sentenceLoading = viewModel.sentenceLoading.value,
                            selectedWord = studyViewModel.selectedWord.value,
                            onSentenceAdd = studyViewModel::setStudyContext,
                            onWordAdd = studyViewModel::setStudyTargetWord,
                            onWordSelect = studyViewModel::setSelectedWord,
                            addSentenceToReview = viewModel::addSentenceToReview,
                            loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
                            unsetSharedSentence = viewModel::unsetSharedSentence,
                            showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )

                    }

                    composable("study_screen") {
                        StudyScreen(
                            context = studyViewModel.currentStudyContext.value ?: "",
                            wordModel = studyViewModel.currentStudyTargetWord.value,
                            similarSentences = studyViewModel.similarSentences.value,
                            wordKanjis = viewModel.currentWordKanjis.value,
                            navController = navController,
                            setCurrentKanji = viewModel::setCurrentKanji,
                            setCurrentStory = viewModel::setCurrentStory,
                            setSharedSentence = viewModel::setSharedSentence,
                            contextLoading = studyViewModel.contextLoading.value,
                            wordLoading = studyViewModel.wordModelLoading.value,
                            sentencesLoading = studyViewModel.similarSentencesLoading.value,
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
    }

    private fun showSnackbar(
        scaffoldState: ScaffoldState,
        message: String,
        actionLabel: String="hide",
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        lifecycleScope.launch{
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }
}