package com.example.nala.ui

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.URLUtil
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
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
import com.example.nala.service.background.ArticleService
import android.provider.Settings
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.nala.R
import com.example.nala.ui.dictionary.DictionaryForegroundService

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: BaseApplication

    // VIEW MODELS
    private val viewModel: DictionaryViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val studyViewModel: StudyViewModel by viewModels()

    @ExperimentalComposeUiApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var startDestination = "home_screen"
        // flag that checks if the dictionary was called from an article
        // TODO improve the checking logic
        var fromLookup = false

        // INTENT MATCHING
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)){
                    viewModel.setSharedText(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) )
                    startDestination = "detail_screen"
                    fromLookup = true
                    viewModel.setIsWordFromIntent()
                }
            }
            Intent.ACTION_SEND -> {
                Log.d("SHARED", "ACTION SEND CALLED!")
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { url ->
                        Log.d("SHARED", "SHARED TEXT: $url")
                        if(URLUtil.isValidUrl(url)) {
                            //reviewViewModel.addArticleToChronology(url)
                            startCustomTabIntent(url)
                        } else {
                            viewModel.setSharedSentence(url)
                            startDestination = "sentence_form_screen"
                            fromLookup = true
                        }
                    }
                } else {
                    Log.d("SHARED", "DIDNT PROCESS TEXT!")
                }
            }
            else -> {
                // DONT BOTHER
            }
        }

        // VIEW SETTING
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContent{
            AppTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()

                // Navigate to detail screen if a word was searched from another app


                NavHost(navController=navController, startDestination) {
                    composable("home_screen"){
                        HomeScreen(
                            query = viewModel.query.value,
                            mightForgetItemsState = viewModel.mightForgetItemsState.value,
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
                            searchState = viewModel.wordSearchState.value,
                            fromLookup = fromLookup,
                            navController = navController,
                            wordKanjis = viewModel.currentWordKanjis.value,
                            setCurrentKanji = viewModel::setCurrentKanji,
                            setCurrentStory = viewModel::setCurrentStory,
                            unsetSharedWord = viewModel::unsetSharedText,
                            addToReview =  viewModel::addWordToReview,
                            loadWordReviews = reviewViewModel::loadWordReviewItems,
                            isWordFromIntent = viewModel.isWordFromIntent.value,
                            isWordFromForm = viewModel.isWordFromForm.value,
                            scaffoldState = scaffoldState,
                            showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                        )
                    }

                    composable("kanji_detail_screen"){
                        KanjiDetailScreen(
                            kanjiSearchState = viewModel.kanjiSearchState.value,
                            kanjiStoryState = viewModel.kanjiStoryState.value,
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
                            selectedCategory = reviewViewModel.selectedCategory.value,
                            setCategory = reviewViewModel::setCategory,
                            wordReviewItems = reviewViewModel.wordReviewItems.value,
                            sentenceReviewItems = reviewViewModel.sentenceReviewItems.value,
                            kanjiReviewItems = reviewViewModel.kanjiReviewItems.value,
                            setWordItem = viewModel::setCurrentWordFromReview,
                            setSentenceItem = studyViewModel::setStudyContext,
                            setTargetWordItem = studyViewModel::setStudyTargetWord,
                            setKanjiItem = viewModel::setCurrentKanji,
                            removeWordReview = reviewViewModel::removeWordReviewItem,
                            removeSentenceReview = reviewViewModel::removeSentenceReviewItem,
                            removeKanjiReview = reviewViewModel::removeKanjiReviewItem,
                            dismissWordReview = {},
                            dismissSentenceReview = {},
                            dismissKanjiReview = {},
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
                            sentenceState = viewModel.sentenceState.value,
                            tokens = viewModel.sharedSentenceTokens.value,
                            tokensIndexMap = viewModel.sharedSentenceTokensIndexMap.value,
                            fromLookup = fromLookup,
                            selectedWord = studyViewModel.selectedWord.value,
                            onSentenceAdd = studyViewModel::setStudyContext,
                            setKanjis = studyViewModel::setCurrentWordKanjis,
                            onWordAdd = studyViewModel::setStudyTargetWord,
                            onWordSelect = studyViewModel::setSelectedWord,
                            addSentenceToReview = viewModel::addSentenceToReview,
                            loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
                            unsetSelectedWord = studyViewModel::unsetSelectedWord,
                            unsetSharedSentence = viewModel::unsetSharedSentence,
                            showSnackbar = {showSnackbar(scaffoldState, message="Added to review")},
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )

                    }

                    composable("study_screen") {
                        StudyScreen(
                            studyContextState = studyViewModel.studyContextState.value,
                            targetWordState = studyViewModel.targetWordState.value,
                            similarSentencesState = studyViewModel.similarSentencesState.value,
                            navController = navController,
                            setSharedSentence = viewModel::setSharedSentence,
                            setCurrentWord = viewModel::setCurrentWordFromStudy,
                            unsetTargetWord = studyViewModel::unsetSelectedWord,
                            addSentenceToReview = viewModel::addSentenceToReview,
                            loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
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
    }

    private fun startDictionaryWindowService(word: String) {
        val dictionaryIntent = Intent(this, DictionaryForegroundService::class.java)
        dictionaryIntent.putExtra("word", word)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(dictionaryIntent)
                } else {
                    startService(dictionaryIntent)
                }
            }
        } else {
            startService(dictionaryIntent)
        }
    }

    private fun startCustomTabIntent(url: String) {
        // Create explicit intent
        val addToFavoritesIntent = Intent(this, ArticleService::class.java)
        addToFavoritesIntent.putExtra("url", url)
        val pendingAddToFavorites = PendingIntent.getActivity(
            this,0 ,addToFavoritesIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Set the action button
        val builder = CustomTabsIntent.Builder();
        val bitmap = (ResourcesCompat.getDrawable(
             this.resources,
             R.drawable.save_to_favorites_icon,
             null) as VectorDrawable).toBitmap()
         builder.setActionButton(
             bitmap,
             "add to favorites",
             pendingAddToFavorites,
             true)
        val customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    // method to ask user to grant the Overlay permission
    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(myIntent)
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