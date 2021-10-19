package com.example.nala.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.nala.R
import com.example.nala.ui.dictionary.DictionaryForegroundService
import androidx.compose.material.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import android.widget.Toast
import androidx.navigation.NavController
import com.example.nala.services.audio.MediaCaptureService
import com.example.nala.ui.ocr.OCRViewModel
import com.example.nala.utils.NetworkConstants

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val MEDIA_PROJECTION_REQUEST_CODE = 13
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 42
    }

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
    private val ocrViewModel: OCRViewModel by viewModels()

    //private lateinit var ocrService: FallbackScreenshotService

    // GOOGLE AUTH SERVICE
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleAuthenticator: GoogleAuthenticator

    // AUDIO RECORDING SERVICE
    private lateinit var mediaProjectionManager: MediaProjectionManager

    // ROUTING VARIABLES
    private var startDestination = "home_screen"
    // flag that checks if the dictionary was called from an article
    var fromLookup = false

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
        ocrViewModel.initModel()
        //ocrService = FallbackScreenshotService(this)
        val targetLangs =
            getSharedPreferences("langs", Context.MODE_PRIVATE)
                .getStringSet("target_langs", setOf())?.toSet() ?: setOf()
        ytViewModel.setTargetLangs(targetLangs)
        // INTENT MATCHING
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                println("ACTION PROCESS TEXT")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    handleSharedWord()
                }
            }
            Intent.ACTION_SEND -> {
                println("ACTION SEND")
                handleSharedText(intent)
            }
            else -> {
                Log.d("INTENTDEBUG", "Action triggered: ${intent?.action}")
                Log.d("INTENTDEBUG", "CATEGORIES: ${intent.categories}")
                Log.d("INTENTDEBUG", "DATA: ${intent.data}")
                Log.d("INTENTDEBUG", "DATA STRING: ${intent.dataString}")
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
                            authPending = authViewModel.authRequestPending.value,
                            onQueryChange = dictViewModel::onQueryChanged,
                            onClick = {dictViewModel.onTriggerEvent(DictionaryEvent.SearchWordEvent)},
                            onSignIn = googleAuthenticator::signIn,
                            onSignOut = { onLogOut() },
                            onSetMFI = dictViewModel::setCurrentWordFromReview,
                            textReceived = dictViewModel.textReceived.value,
                            sentenceReceived = dictViewModel.sentenceReceived.value,
                            isHomeSelected = dictViewModel.isHomeSelected.value,
                            isReviewsSelected = dictViewModel.isReviewSelected.value,
                            toggleHome = dictViewModel::toggleHome,
                            toggleReviews = dictViewModel::toggleReviews,
                            onHandleQueryTextType = this@MainActivity::onHandleQueryTextTypes,
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
                            ocrTokens = ocrViewModel.inspectedElementTokens.value,
                            ocrTokensMap = ocrViewModel.inspectedElementTokensMap.value,
                            recognizedSentence = ocrViewModel.recognizedText.value,
                            commentsState = ytViewModel.commentsState.value,
                            videoId = ytViewModel.currentVideoId.value,
                            videoLoading = ytViewModel.videoDataLoading.value,
                            onPause = ytViewModel::setPause,
                            selectedTab = ytViewModel.selectedTab.value,
                            playerPosition = ytViewModel.currentPlayerPosition,
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
                            onTakeScreenshot = ocrViewModel::getTextFromView,
                            onSetView = ocrViewModel::setView,
                            authState = authViewModel.account.value,
                            authPending = authViewModel.authRequestPending.value,
                            onSignIn = googleAuthenticator::signIn,
                            onSignOut = { onLogOut() },
                            activeCaption = ytViewModel.activeCaption.value,
                            loadingDialogOpen = ocrViewModel.loadingDialogOpen.value,
                            setLoadingDialogOpen = ocrViewModel::setLoadingDialogOpen,
                            sentenceDialogOpen = ocrViewModel.sentenceDialogOpen.value,
                            setSentenceDialogOpen = ocrViewModel::setSentenceDialogOpen,
                            onSaveSentence = dictViewModel::addSentenceToReview,
                            onShowAddedSentenceSnackbar = {
                                showSnackbar(
                                    scaffoldState,
                                    message = getString(R.string.sentence_added)
                                )
                            },
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
                            authState = authViewModel.account.value,
                            onSignIn = googleAuthenticator::signIn,
                            onSignOut = { onLogOut() },
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )
                    }
                    
                    composable("videos") {
                        SavedVideosScreen(
                            videos = favoritesViewModel.savedVideosState.value,
                            onRemoveVideo = favoritesViewModel::removeVideoFromFavorites,
                            onSetVideo = ytViewModel::setVideoModelFromCache,
                            authState = authViewModel.account.value,
                            onSignIn = googleAuthenticator::signIn,
                            onSignOut = { onLogOut() },
                            scaffoldState = scaffoldState,
                            navController = navController,
                        )
                    }

                    composable("articles") {
                        SavedArticlesScreen(
                            articles = favoritesViewModel.savedArticlesState.value,
                            onRemoveArticle = favoritesViewModel::removeArticleFromFavorites,
                            onSetArticle = reviewViewModel::setArticleFromCache,
                            authState = authViewModel.account.value,
                            onSignIn = googleAuthenticator::signIn,
                            onSignOut = { onLogOut() },
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
                            authState = authViewModel.account.value,
                            onSignIn = googleAuthenticator::signIn,
                            onSignOut = { onLogOut() },
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
        // AUTH ACTIVITY RESULT
        if (requestCode == GoogleAuthenticator.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    authViewModel.setAuthRequestPending(true)
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
            // AUDIO RECORDING ACTIVITY RESULT
        }
        if (requestCode == MEDIA_PROJECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    this,
                    "MediaProjection permission obtained. Foreground service will be started to capture audio.",
                    Toast.LENGTH_LONG
                ).show()

                val audioCaptureIntent = Intent(this, MediaCaptureService::class.java).apply {
                    action = MediaCaptureService.ACTION_START
                    putExtra(MediaCaptureService.EXTRA_RESULT_DATA, data!!)
                }
                ContextCompat.startForegroundService(this, audioCaptureIntent)

                //setButtonsEnabled(isCapturingAudio = true)
            }
        } else {
            Toast.makeText(
               this, "Request to obtain MediaProjection denied.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ocrViewModel.dispose()
    }

    // OCR
    /*
    private fun recognizeText( ){
        ocrLoading.value = true
        ocrService.recognize()
        Log.d("OCR_DEBUG", "RETURNED TEXT: ${ocrService.getResult()}")
        ocrLoading.value = false
    } */

    // PERMISSIONS

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

    // AUDIO RECORDING FUNCTIONS
    /*
    private fun checkRecordingPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {

            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions,0)
            }
        }
    }

    private fun startCapturing() {
        Toast.makeText(this, "start recording", Toast.LENGTH_LONG).show()
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission()
        } else {
            startMediaProjectionRequest()
        }
    }

    private fun stopCapturing() {
        Toast.makeText(this, "stop recording", Toast.LENGTH_LONG).show()
        //setButtonsEnabled(isCapturingAudio = false)
        val captureIntent = Intent(this, MediaCaptureService::class.java).apply {
            action = MediaCaptureService.ACTION_STOP
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(captureIntent)
        } else {
            startService(captureIntent)
        }
    } */


    private fun isRecordAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permissions to capture audio granted.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "Permissions to capture audio denied.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startMediaProjectionRequest() {
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), MEDIA_PROJECTION_REQUEST_CODE)
    }

    // DICTIONARY POP UP
    private fun startDictionaryWindowService(word: String) {
        val dictionaryIntent = Intent(this, DictionaryForegroundService::class.java).apply{
            putExtra("word", word)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
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
        Log.d("INTENTDEBUG", "ACTION SEND CALLED!")
        when (intent?.type) {
            "text/plain", "*/*" -> {
                Log.d("INTENTDEBUG", "INTENT TYPE: ${intent.type}")
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { inputString ->
                    Log.d("INTENTDEBUG", "SHARED TEXT: $inputString")
                    handleIntentTextTypes(inputString)
                }
            }
            else -> {
                Log.d("INTENTDEBUG", "INTENT EXTRA: ${Intent.EXTRA_TEXT}" )
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun handleIntentTextTypes(inputString: String) {
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

    private fun onHandleQueryTextTypes(inputString: String, navController: NavController) {
        lifecycleScope.launch{
            if(dictViewModel.isTextWord(inputString)) {
                dictViewModel.onTriggerEvent(DictionaryEvent.SearchWordEvent)
                navController.navigate("detail_screen")
            } else {
                when(Utils.parseInputString(inputString)) {
                    InputStringType.Sentence -> {
                        dictViewModel.setSharedSentence(inputString)
                        navController.navigate("sentence_form_screen")
                    }
                    InputStringType.ArticleUrl -> {
                        reviewViewModel.setArticle(inputString)
                        navController.navigate("article_screen")
                    }
                    InputStringType.YoutubeUrl -> {
                        ytViewModel.setVideoModel(inputString)
                        navController.navigate("video_screen")
                    }
                }
            }
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
            this.finish()
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
                .requestScopes(Scope(NetworkConstants.YT_SCOPE_ENTRY_POINT))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)
        googleAuthenticator = GoogleAuthenticator(
            activity = this@MainActivity,
            googleSignInClient = googleSignInClient,
            onSignIn = { authViewModel.setAuthRequestPending(true) },
            onSignOut = authViewModel::invalidateAccount,
        )
    }

    private fun onLogOut() {
        googleAuthenticator.signOut()
        authViewModel.invalidateAccount()
        Toast.makeText(
            this, "Logged out",
            Toast.LENGTH_SHORT
        ).show()
    }

    /*
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
   } */

}