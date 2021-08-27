package com.example.nala.ui

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.VectorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.nala.services.background.ArticleService
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.nala.R
import com.example.nala.ui.dictionary.DictionaryForegroundService
import androidx.compose.material.*
import com.example.nala.ui.composables.articles.ArticleScreen
import com.example.nala.ui.composables.dictionary.DictionaryDetailScreen
import com.example.nala.ui.composables.dictionary.HomeScreen
import com.example.nala.ui.composables.dictionary.KanjiDetailScreen
import com.example.nala.ui.composables.review.ReviewListScreen
import com.example.nala.ui.composables.saved.SavedArticlesScreen
import com.example.nala.ui.composables.saved.SavedVideosScreen
import com.example.nala.ui.composables.settings.SettingsScreen
import com.example.nala.ui.composables.study.OneTargetForm
import com.example.nala.ui.composables.study.StudyScreen
import com.example.nala.ui.composables.yt.VideoScreen
import com.example.nala.ui.favorites.FavoritesViewModel
import com.example.nala.ui.settings.SettingsViewModel
import com.example.nala.ui.yt.YoutubeViewModel
import com.example.nala.utils.InputStringType
import com.example.nala.utils.Utils
import kotlinx.coroutines.Dispatchers


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
    private val settingsViewModel: SettingsViewModel by viewModels()

    // ROUTING VARIABLES
    var startDestination = "home_screen"
    // flag that checks if the dictionary was called from an article
    var fromLookup = false

    @RequiresApi(Build.VERSION_CODES.M)
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SHARE", "ON CREATE CALLED")
        // NEEDED TO USE POP UP DICTIONARY MODE
        checkOverlayPermissions()
        // SETTING UP NEEDED OBSERVABLES
        viewModel.loadMightForgetItems()
        settingsViewModel.loadSharedPreferences()
        favoritesViewModel.loadSavedVideos()
        favoritesViewModel.loadSavedArticles()

        // INTENT MATCHING
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                handleSharedWord()
            }
            Intent.ACTION_SEND -> {
                handleSharedText(intent)
            }
            else -> {
                Log.d("INTENTDEBUG", "Action triggered: ${intent?.action}")
                // DONT BOTHER
            }
        }

        // VIEW SETTING
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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
                            scaffoldState = scaffoldState,
                            navController = navController
                        )
                    }

                    composable("detail_screen") {
                        DictionaryDetailScreen(
                            searchState = viewModel.wordSearchState.value,
                            onRetry = viewModel::retrySearch,
                            navController = navController,
                            wordKanjis = viewModel.currentWordKanjis.value,
                            setCurrentKanji = viewModel::setCurrentKanji,
                            setCurrentStory = viewModel::setCurrentStory,
                            addToReview =  viewModel::addWordToReview,
                            loadWordReviews = reviewViewModel::loadWordReviewItems,
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
                            availableTracks = ytViewModel.availableCaptionTracks.value,
                            isVideoSaved = ytViewModel.isVideoSaved.value,
                            onSetVideoAsSaved = ytViewModel::setIsVideoSaved,
                            inspectedCaption = ytViewModel.inspectedCaption.value,
                            inspectedComment = ytViewModel.inspectedComment.value,
                            onLoadCaptions = ytViewModel::loadCaptions,
                            onLoadComments = ytViewModel::loadComments,
                            onUpdateComments = ytViewModel::updateComments,
                            isUpdatingComments = ytViewModel.isUpdatingComments.value,
                            onLoadTrack = ytViewModel::onLoadTrack,
                            onAddVideoToFavorites = ytViewModel::addVideoToFavorites,
                            onRemoveVideoFromFavorites = ytViewModel::removeVideoFromFavorites,
                            onSetInspectedCaption = ytViewModel::onInspectCaption,
                            onSetInspectedComment = ytViewModel::onInspectComment,
                            onSetSelectedWord = ytViewModel::setSelectedWord,
                            checkNetworkAvailable = ytViewModel::checkNetworkAvailable,
                            selectedWord = ytViewModel.inspectedElementSelectedWord.value,
                            tokens = ytViewModel.inspectedElementTokens.value,
                            tokensMap = ytViewModel.inspectedElementTokensMap.value,
                            commentsState = ytViewModel.commentsState.value,
                            videoId = ytViewModel.currentVideoId.value,
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
                            onShowSavedSnackBar = { showSnackbar(scaffoldState, message="Added to favorites") },
                            onShowRemovedSnackBar = { showSnackbar(scaffoldState, message="Removed from favorites") },
                            onRetry = ytViewModel::onRetry,
                            activeCaption = ytViewModel.activeCaption.value,
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )
                    }

                    composable("article_screen") {
                        ArticleScreen(
                            article = reviewViewModel.currentArticleUrl.value,
                            articleLoaded = reviewViewModel.isArticleLoaded.value,
                            isSaved = reviewViewModel.isArticleSaved.value,
                            onSaveArticle = favoritesViewModel::addArticleToFavorites,
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )
                    }
                    
                    composable("videos") {
                        SavedVideosScreen(
                            videos = favoritesViewModel.savedVideosState.value,
                            onRemoveVideo = favoritesViewModel::removeVideoFromFavorites,
                            onSetVideo = ytViewModel::setVideoModelFromCache,
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )
                    }

                    composable("articles") {
                        SavedArticlesScreen(
                            articles = favoritesViewModel.savedArticlesState.value,
                            onRemoveArticle = favoritesViewModel::removeArticleFromFavorites,
                            onSetArticle = reviewViewModel::setArticleFromCache,
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            isJapaneseSelected = settingsViewModel.isJapaneseSelected.value,
                            isEnglishSelected = settingsViewModel.isEnglishSelected.value,
                            isFrenchSelected = settingsViewModel.isFrenchSelected.value,
                            isSpanishSelected = settingsViewModel.isSpanishSelected.value,
                            setLangSelected = settingsViewModel::setLangSelected,
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
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
        val builder = CustomTabsIntent.Builder()
        val bitmap = (ResourcesCompat.getDrawable(
             this.resources,
             R.drawable.save_to_favorites_icon,
             null) as VectorDrawable).toBitmap()
         builder.setActionButton(
             bitmap,
             "add to favorites",
             pendingAddToFavorites,
             true)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
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

    private fun handleSharedText(intent: Intent?) {
        Log.d("SHARED", "ACTION SEND CALLED!")
        if ("text/plain" == intent?.type) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { inputString ->
                Log.d("SHARED", "SHARED TEXT: $inputString")
                when(Utils.parseInputString(inputString)) {
                    InputStringType.Sentence -> {
                        viewModel.setSharedSentence(inputString)
                        startDestination = "sentence_form_screen"
                        fromLookup = true
                    }
                    InputStringType.ArticleUrl -> {
                        reviewViewModel.setArticle(inputString)
                        startDestination = "article_screen"
                    }
                    InputStringType.YoutubeUrl -> {
                        Log.d("YOUTUBEDEBUG", "URL: $inputString")
                        ytViewModel.setVideoModel(inputString)
                        startDestination = "video_screen"
                    }
                }
            }
        } else {
            Log.d("SHARED", "DIDNT PROCESS TEXT!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
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