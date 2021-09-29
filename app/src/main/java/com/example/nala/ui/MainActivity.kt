package com.example.nala.ui

import android.app.Activity
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.nala.R
import com.example.nala.ui.dictionary.DictionaryForegroundService
import androidx.compose.material.*
import com.example.nala.BuildConfig
import com.example.nala.services.auth.GoogleAuthenticator
import com.example.nala.ui.auth.AuthViewModel
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: BaseApplication

    // VIEW MODELS
    private val dictViewModel: DictionaryViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()
    private val studyViewModel: StudyViewModel by viewModels()
    private val ytViewModel: YoutubeViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    // GOOGLE AUTH
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleAuthenticator: GoogleAuthenticator

    // ROUTING VARIABLES
    var startDestination = "home_screen"
    // flag that checks if the dictionary was called from an article
    var fromLookup = false

    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.M)
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGoogleAuth()
        // NEEDED TO USE POP UP DICTIONARY MODE
        checkOverlayPermissions()
        // SETTING UP NEEDED OBSERVABLES
        dictViewModel.loadMightForgetItems()
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
                            query = dictViewModel.query.value,
                            mightForgetItemsState = dictViewModel.mightForgetItemsState.value,
                            authState = authViewModel.account.value,
                            onQueryChange = dictViewModel::onQueryChanged,
                            onClick = {dictViewModel.onTriggerEvent(DictionaryEvent.SearchWordEvent)},
                            onSignIn = googleAuthenticator::signIn,
                            onSignOut = { onLogOut() },
                            textReceived = dictViewModel.textReceived.value,
                            sentenceReceived = dictViewModel.sentenceReceived.value,
                            isHomeSelected = dictViewModel.isHomeSelected.value,
                            isReviewsSelected = dictViewModel.isReviewSelected.value,
                            toggleHome = dictViewModel::toggleHome,
                            toggleReviews = dictViewModel::toggleReviews,
                            scaffoldState = scaffoldState,
                            navController = navController
                        )
                    }

                    composable("detail_screen") {
                        DictionaryDetailScreen(
                            searchState = dictViewModel.wordSearchState.value,
                            onRetry = dictViewModel::retrySearch,
                            navController = navController,
                            wordKanjis = dictViewModel.currentWordKanjis.value,
                            setCurrentKanji = dictViewModel::setCurrentKanji,
                            setCurrentStory = dictViewModel::setCurrentStory,
                            addToReview =  dictViewModel::addWordToReview,
                            loadWordReviews = reviewViewModel::loadWordReviewItems,
                            scaffoldState = scaffoldState,
                            onShare = this@MainActivity::shareText,
                            showSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.added_to_review),
                                )
                            },
                        )
                    }

                    composable("kanji_detail_screen"){
                        KanjiDetailScreen(
                            kanjiSearchState = dictViewModel.kanjiSearchState.value,
                            kanjiStoryState = dictViewModel.kanjiStoryState.value,
                            storyFormActive = dictViewModel.editStoryFormActive.value,
                            addKanjiToReview = dictViewModel::addKanjiToReview,
                            updateKanjiStory = dictViewModel::updateKanjiStory,
                            setCurrentStory = dictViewModel::setCurrentStory,
                            toggleStoryEditForm = dictViewModel::toggleEditStoryForm,
                            navController = navController,
                            scaffoldState = scaffoldState,
                            onShare = this@MainActivity::shareText,
                            showSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.added_to_review)
                                )
                            },
                        )
                    }

                    composable("review_screen") {
                        ReviewListScreen(
                            selectedCategory = reviewViewModel.selectedCategory.value,
                            setCategory = reviewViewModel::setCategory,
                            wordReviewItems = reviewViewModel.wordReviewItems.value,
                            sentenceReviewItems = reviewViewModel.sentenceReviewItems.value,
                            kanjiReviewItems = reviewViewModel.kanjiReviewItems.value,
                            setWordItem = dictViewModel::setCurrentWordFromReview,
                            setSentenceItem = studyViewModel::setStudyContext,
                            setTargetWordItem = studyViewModel::setStudyTargetWord,
                            setKanjiItem = dictViewModel::setCurrentKanji,
                            removeWordReview = reviewViewModel::removeWordReviewItem,
                            removeSentenceReview = reviewViewModel::removeSentenceReviewItem,
                            removeKanjiReview = reviewViewModel::removeKanjiReviewItem,
                            dismissWordReview = {},
                            dismissSentenceReview = {},
                            dismissKanjiReview = {},
                            isHomeSelected = dictViewModel.isHomeSelected.value,
                            isReviewsSelected = dictViewModel.isReviewSelected.value,
                            toggleHome = dictViewModel::toggleHome,
                            toggleReviews = dictViewModel::toggleReviews,
                            updateWordReviewItem = reviewViewModel::updateWordReviewItem,
                            updateSentenceReviewItem= reviewViewModel::updateSentenceReviewItem,
                            updateKanjiReviewItem = reviewViewModel::updateKanjiReviewItem,
                            onShare = this@MainActivity::shareText,
                            onSearch = reviewViewModel::search,
                            onRestore = reviewViewModel::restore,
                            navController = navController,
                            showSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = R.string.review_removed.toString(),
                                    actionLabel="Undo",
                                )},
                            scaffoldState = scaffoldState,
                        )
                    }

                    composable("sentence_form_screen"){
                        OneTargetForm(
                            sentenceState = dictViewModel.sentenceState.value,
                            tokens = dictViewModel.sharedSentenceTokens.value,
                            tokensIndexMap = dictViewModel.sharedSentenceTokensIndexMap.value,
                            fromLookup = fromLookup,
                            selectedWord = studyViewModel.selectedWord.value,
                            onSentenceAdd = studyViewModel::setStudyContext,
                            setKanjis = studyViewModel::setCurrentWordKanjis,
                            onWordAdd = studyViewModel::setStudyTargetWord,
                            onWordSelect = studyViewModel::setSelectedWord,
                            addSentenceToReview = dictViewModel::addSentenceToReview,
                            loadSentenceReviews = reviewViewModel::loadSentenceReviewItems,
                            unsetSelectedWord = studyViewModel::unsetSelectedWord,
                            unsetSharedSentence = dictViewModel::unsetSharedSentence,
                            showSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.added_to_review)
                                )
                            },
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
                            setSharedSentence = dictViewModel::setSharedSentence,
                            setCurrentWord = dictViewModel::setCurrentWordFromStudy,
                            unsetTargetWord = studyViewModel::unsetSelectedWord,
                            addSentenceToReview = dictViewModel::addSentenceToReview,
                            loadSimilarSentences = studyViewModel::loadSimilarSentences,
                            onShare = this@MainActivity::shareText,
                            setIsWordFromForm = dictViewModel::setIsWordFromForm,
                            scaffoldState = scaffoldState,
                            showReviewSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.added_to_review),
                                )
                            },
                            showSaveSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.sentence_added)
                                )
                            }
                        )
                    }

                    composable("video_screen") {
                        VideoScreen(
                            lifecycle = lifecycle,
                            captionsState = ytViewModel.captionsState.value,
                            availableTracks = ytViewModel.availableCaptionTracks.value,
                            isVideoSaved = ytViewModel.isVideoInFavorites.value,
                            onSetVideoAsSaved = ytViewModel::setVideoInFavorites,
                            inspectedCaption = ytViewModel.inspectedCaption.value,
                            inspectedComment = ytViewModel.inspectedComment.value,
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
                            onInitPlayer = ytViewModel::initPlayer,
                            onPlayerTimeElapsed = ytViewModel::onPlayerTimeElapsed,
                            onChangeSelectedTab = ytViewModel::setSelectedTab,
                            onShowCaptionsDetails = dictViewModel::setSharedSentence,
                            onShowCommentsDetails = dictViewModel::setSharedSentence,
                            onSearchWord = {
                                startDictionaryWindowService(ytViewModel.inspectedElementSelectedWord.value)
                            },
                            onSetPlayerPosition = ytViewModel::setPlayerPosition,
                            onShowSavedSnackBar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.added_to_favorites)
                                )
                            },
                            onShowRemovedSnackBar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.removed_from_favorites)
                                )
                            },
                            onRetry = ytViewModel::onRetry,
                            onRequestLogin = googleAuthenticator::signIn,
                            authState = authViewModel.account.value,
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
                            onSaveArticle = reviewViewModel::addArticleToFavorites,
                            onRemoveArticle = reviewViewModel::removeArticleFromFavorites,
                            onSetIsArticleSaved = reviewViewModel::setIsArticleInFavorites,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GoogleAuthenticator.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("AUTHDEBUG", "User: ${account?.displayName}")
                    Log.d("AUTHDEBUG", "Email: ${account?.email}")
                    Log.d("AUTHDEBUG", "Profile pic: ${account?.photoUrl}")
                    Log.d("AUTHDEBUG", "Auth Code: ${account?.serverAuthCode ?: "Account is NULL"}")
                    authViewModel.setAccount(account)
                } catch (e: ApiException) {
                    Log.d("AUTHDEBUG", "ERROR: $e")
                    authViewModel.setAuthError()
                }
            }
        }
    }

    private fun checkOverlayPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                showOverlayPermissionsDialog()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showOverlayPermissionsDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        dialogBuilder.apply{
            setMessage(R.string.dialog_message)
            setTitle(R.string.dialog_title)
            setPositiveButton(R.string.ok
            ) { _, _ ->
                val permIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(permIntent)
            }
            setNegativeButton(R.string.cancel
            ) { dialog, _ ->
                dialog.dismiss()
            }
        }
        dialogBuilder.show()
    }

    private fun startDictionaryWindowService(word: String) {
        val dictionaryIntent = Intent(this, DictionaryForegroundService::class.java)
        dictionaryIntent.putExtra("word", word)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
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

    @ExperimentalCoroutinesApi
    private fun handleSharedText(intent: Intent?) {
        Log.d("SHARED", "ACTION SEND CALLED!")
        if (intent?.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { inputString ->
                Log.d("SHARED", "SHARED TEXT: $inputString")
                when(Utils.parseInputString(inputString)) {
                    InputStringType.Sentence -> {
                        dictViewModel.setSharedSentence(inputString)
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
            //checkOverlayPermissions()
            val word = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""
            startDictionaryWindowService(word)
            finish()
        }
    }

    private fun shareText(text: String?) {
        text?.let{
            val shareIntent = Intent().apply{
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, it)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share word/sentence to...") )
        }
    }

    private fun initGoogleAuth() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(BuildConfig.OAUTH_ID)
                .requestScopes(Scope("https://www.googleapis.com/auth/youtube.force-ssl"))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleAuthenticator = GoogleAuthenticator(
            this,
            googleSignInClient,
            authViewModel::invalidateAccount
        )
    }

    private fun onLogOut() {
        googleAuthenticator.signOut()
        authViewModel.invalidateAccount()
    }


}