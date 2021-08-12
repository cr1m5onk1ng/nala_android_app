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
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.*
import com.example.nala.ui.composables.dictionary.DictionaryDetailScreen
import com.example.nala.ui.composables.dictionary.HomeScreen
import com.example.nala.ui.composables.dictionary.KanjiDetailScreen
import com.example.nala.ui.composables.review.ReviewListScreen
import com.example.nala.ui.composables.saved.SavedArticlesScreen
import com.example.nala.ui.composables.saved.SavedVideosScreen
import com.example.nala.ui.composables.study.OneTargetForm
import com.example.nala.ui.composables.study.StudyScreen
import com.example.nala.ui.composables.yt.VideoScreen
import com.example.nala.ui.favorites.FavoritesViewModel
import com.example.nala.ui.yt.YoutubeViewModel
import com.example.nala.utils.InputStringType
import com.example.nala.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: BaseApplication

    // VIEW MODELS
    private val viewModel: DictionaryViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val studyViewModel: StudyViewModel by viewModels()
    private val ytViewModel: YoutubeViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    // ROUTING VARIABLES
    var startDestination = "home_screen"
    // flag that checks if the dictionary was called from an article
    var fromLookup = false

    var isArticle = false

    @ExperimentalComposeUiApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INTENT MATCHING
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                handleSharedWord()
            }
            Intent.ACTION_SEND -> {
                handleSharedText()
            }
            else -> {
                // DONT BOTHER
            }
        }

        if(isArticle) return

        // VIEW SETTING
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContent{
            AppTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

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
                            onMinimize = { startDictionaryWindowService("") },
                            onCheckPermissions = { checkOverlayPermissions() },
                            scaffoldState = scaffoldState,
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

                    composable("video_screen") {
                        VideoScreen(
                            lifecycle = lifecycle,
                            captionsState = ytViewModel.captionsState.value,
                            inspectedCaption = ytViewModel.inspectedCaption.value,
                            inspectedComment = ytViewModel.inspectedComment.value,
                            onLoadCaptions = ytViewModel::loadCaptions,
                            onLoadComments = ytViewModel::loadComments,
                            onAddVideoToFavorites = favoritesViewModel::addVideoToFavorites,
                            onSetInspectedCaption = ytViewModel::onInspectCaption,
                            onSetInspectedComment = ytViewModel::onInspectComment,
                            onSetSelectedWord = ytViewModel::setSelectedWord,
                            selectedWord = ytViewModel.inspectedElementSelectedWord.value,
                            tokens = ytViewModel.inspectedElementTokens.value,
                            tokensMap = ytViewModel.inspectedElementTokensMap.value,
                            commentsState = ytViewModel.commentsState.value,
                            videoData = ytViewModel.currentVideoData.value,
                            videoLoading = ytViewModel.videoDataLoading.value,
                            player = ytViewModel.ytPlayer.value,
                            selectedTab = ytViewModel.selectedTab.value,
                            playerPosition = ytViewModel.currentPlayerPosition.value,
                            onSaveVideo = {},
                            onInitPlayer = ytViewModel::initPlayer,
                            onPlayerTimeElapsed = ytViewModel::onPlayerTimeElapsed,
                            onClickCaption = ytViewModel::onSeekTo,
                            onChangeSelectedTab = ytViewModel::setSelectedTab,
                            onShowCaptionsDetails = viewModel::setSharedSentence,
                            onShowCommentsDetails = viewModel::setSharedSentence,
                            onSearchWord = {
                                startDictionaryWindowService(ytViewModel.inspectedElementSelectedWord.value)
                                           },
                            onSetPlayerPosition = ytViewModel::setPlayerPosition,
                            activeCaption = ytViewModel.activeCaption.value,
                            navController = navController,
                        )
                    }
                    
                    composable("videos") {
                        favoritesViewModel.loadSavedVideos()
                        SavedVideosScreen(
                            videos = favoritesViewModel.savedVideoState.value,
                            onRemoveVideo = favoritesViewModel::removeVideoFromFavorites,
                        )
                    }

                    composable("articles") {
                        SavedArticlesScreen(
                            articles = favoritesViewModel.savedArticlesState.value,
                            onRemoveArticle = favoritesViewModel::removeArticleFromFavorites,
                        )
                    }
                }
            }
        }
    }

    private fun openWebView(url: String) {
        val articleView = findViewById<WebView>(R.id.articleView)
        articleView.apply{
            webViewClient = WebViewClient()
            loadUrl(url)
        }
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            reviewViewModel.saveArticle(url)
            Snackbar.make(articleView, "Article saved", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun checkOverlayPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                val permIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(permIntent)
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
                startService(dictionaryIntent)
            }
        } else {
            startService(dictionaryIntent)
        }
    }

    private fun startCustomTabIntent(url: String) {
        // Create intent
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

    private fun handleSharedText() {
        Log.d("SHARED", "ACTION SEND CALLED!")
        if ("text/plain" == intent.type) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { inputString ->
                Log.d("SHARED", "SHARED TEXT: $inputString")
                val sharedStringType = Utils.parseInputString(inputString)
                when(sharedStringType) {
                    InputStringType.Sentence -> {
                        viewModel.setSharedSentence(inputString)
                        startDestination = "sentence_form_screen"
                        fromLookup = true
                    }
                    InputStringType.ArticleUrl -> {
                        isArticle = true
                        setContentView(R.layout.article_view)
                        openWebView(inputString)
                    }
                    InputStringType.YoutubeUrl -> {
                        Log.d("YOUTUBEDEBUG", "URL: $inputString")
                        val videoId = Utils.parseVideoIdFromUrl(inputString)
                        Log.d("YOUTUBEDEBUG", "VIDEO ID: $inputString")
                        ytViewModel.setVideoModel(videoId, inputString)
                        //ytViewModel.loadCaptions()
                        //ytViewModel.loadComments()
                        startDestination = "video_screen"
                    }
                }
            }
        } else {
            Log.d("SHARED", "DIDNT PROCESS TEXT!")
        }
    }

    private fun handleSharedWord() {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)){
            /*
            viewModel.setSharedText(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) )
            startDestination = "detail_screen"
            fromLookup = true
            viewModel.setIsWordFromIntent() */
            checkOverlayPermissions()
            val word = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""
            startDictionaryWindowService(word)
            finish()
        }
    }



}