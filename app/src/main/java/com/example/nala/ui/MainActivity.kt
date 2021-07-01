package com.example.nala.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.WindowManager
import android.webkit.URLUtil
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
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
import android.os.Handler
import android.view.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: BaseApplication

    lateinit var myWebView: WebView
    private val viewModel: DictionaryViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val studyViewModel: StudyViewModel by viewModels()

    @ExperimentalComposeUiApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var startDestination = "home_screen"
        // flag that checks if the dictionary was called from an article
        var fromLookup = false
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)){
                    viewModel.setSharedText(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) )
                    startDestination = "detail_screen"
                    fromLookup = true
                }
            }
            Intent.ACTION_SEND -> {
                Log.d("SHARED", "ACTION SEND CALLED!")
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        Log.d("SHARED", "SHARED TEXT: $it")
                        if(URLUtil.isValidUrl(it)) {
                            startCustomTabIntent(it)
                        } else {
                            viewModel.setSharedSentence(it)
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
                            fromLookup = fromLookup,
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
                            context = studyViewModel.currentStudyContext.value ?: "",
                            wordModel = studyViewModel.currentStudyTargetWord.value,
                            similarSentences = studyViewModel.similarSentences.value,
                            wordKanjis = studyViewModel.currentTargetWordKanjis.value,
                            navController = navController,
                            setCurrentKanji = viewModel::setCurrentKanji,
                            setCurrentStory = viewModel::setCurrentStory,
                            setSharedSentence = viewModel::setSharedSentence,
                            setCurrentWord = viewModel::setCurrentWordFromStudy,
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
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        val menu: Menu = mode!!.menu

        // you can remove original menu: copy, cut, select all, share ... or not

        // you can remove original menu: copy, cut, select all, share ... or not
        menu.clear()

        // here i will get text selection by user

        // here i will get text selection by user
        menu.add(com.example.nala.R.string.app_name)
            .setEnabled(true)
            .setVisible(true)
            .setOnMenuItemClickListener { item ->
                if (myWebView != null) {
                    myWebView.evaluateJavascript("window.getSelection().toString()") { value ->
                        if (value != null) {
                            Log.d("WEBVIEW", "Value is: $value")
                        }
                    }
                }
                // Post a delayed runnable to avoid a race condition
                // between evaluateScript() result and mode.finish()
                Handler().postDelayed(Runnable { mode.finish() }, 200)
                true
            }
        super.onActionModeStarted(mode)
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

    private fun startCustomTabIntent(url: String) {
        // Create in-app intent
    /*
        val sendTextIntent = Intent(this, DictionaryActivity::class.java)
        sendTextIntent.setType("text/plain")
        sendTextIntent.action = Intent.ACTION_PROCESS_TEXT
        sendTextIntent.putExtra(Intent.EXTRA_SUBJECT,"Text sent for lookup")
        val pendingSendText = PendingIntent.getActivity(
            this,0,sendTextIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        // Set the action button

     */
        val builder = CustomTabsIntent.Builder();

        //builder.addMenuItem("dictionary", pendingSendText)

        val customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

}