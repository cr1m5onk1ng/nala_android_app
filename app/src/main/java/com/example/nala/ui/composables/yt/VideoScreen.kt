package com.example.nala.ui.composables.yt

import android.util.Log
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.domain.model.auth.UserModel
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.domain.model.yt.*
import com.example.nala.network.model.menus.ActionModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.*
import com.example.nala.ui.composables.dialogs.LoadingDialog
import com.example.nala.ui.composables.dialogs.SentenceLookupDialog
import com.example.nala.ui.composables.menus.CustomDrawer
import com.example.nala.ui.composables.menus.CustomTopBar
import com.example.nala.ui.theme.Blue500
import com.example.nala.ui.yt.YoutubePlaybackListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LazyListState.isLastVisibleItem(index: Int) =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == index

fun LazyListState.isItemVisible(index: Int): Boolean {
    val firstVisible = layoutInfo.visibleItemsInfo.firstOrNull()?.index
    val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index
    if(firstVisible != null && lastVisible != null) {
        return index >= firstVisible && index < lastVisible
    }
    return false
}


@Composable
fun VideoScreen(
    lifecycle: Lifecycle,
    videoId: String,
    availableTracks: List<YoutubeCaptionTracksModel>,
    isVideoSaved: Boolean,
    onSetVideoAsSaved: (Boolean) -> Unit,
    checkNetworkAvailable: () -> Boolean,
    videoLoading: Boolean,
    onPause: () -> Unit,
    selectedTab: Int,
    captionsState: DataState<List<YoutubeCaptionModel>>,
    commentsState: DataState<YoutubeCommentsList>,
    inspectedCaption: YoutubeCaptionModel?,
    inspectedComment: YoutubeCommentModel?,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    ocrTokens: List<String>,
    ocrTokensMap: Map<Pair<Int, Int>, String>,
    recognizedSentence: String,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    playerPosition: Float,
    onLoadComments: (String?) -> Unit,
    onUpdateComments: () -> Unit,
    isUpdatingComments: Boolean,
    onLoadTrack: (String) -> Unit,
    onAddVideoToFavorites: () -> Unit,
    onRemoveVideoFromFavorites: () -> Unit,
    onInitPlayer: (YouTubePlayer) -> Unit,
    onPlayerTimeElapsed: (Float) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onChangeSelectedTab: (Int) -> Unit,
    onShowCaptionsDetails: (String) -> Unit,
    onShowCommentsDetails: (String) -> Unit,
    onSearchWord: () -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    onShowSavedSnackBar: () -> Unit,
    onShowRemovedSnackBar: () -> Unit,
    onRetry: () -> Unit,
    onRequestLogin: () -> Unit,
    onTakeScreenshot: () -> Unit,
    onSetView: (View) -> Unit,
    activeCaption: Int,
    loadingDialogOpen: Boolean,
    setLoadingDialogOpen: (Boolean) -> Unit,
    setSentenceDialogOpen: (Boolean) -> Unit,
    sentenceDialogOpen: Boolean,
    onSaveSentence: (String, String) -> Unit,
    onShowAddedSentenceSnackbar: () -> Unit,
    authState: AuthState<UserModel?>,
    authPending: Boolean,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController
){
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.video_screen_header),
                scope = scope,
                backgroundColor = MaterialTheme.colors.primary,
                scaffoldState = scaffoldState,
                navController = navController,
                actions = listOf(
                    ActionModel(
                        icon = Icons.Rounded.Favorite,
                        action = {
                            if(!isVideoSaved) {
                                onAddVideoToFavorites()
                                onSetVideoAsSaved(true)
                                onShowSavedSnackBar()
                            } else {
                                onRemoveVideoFromFavorites()
                                onSetVideoAsSaved(false)
                                onShowRemovedSnackBar()
                            }
                        },
                        isActive = isVideoSaved,
                    ),
                    ActionModel(
                        icon = Icons.Rounded.Screenshot,
                        action = {
                            onTakeScreenshot()
                            onPause()
                        },
                        isActive = false,
                    ),
                )
            )
        },
        drawerContent = {
            CustomDrawer(
                modifier = Modifier.background(color = Color.White),
                scope = scope,
                authState = authState,
                onSignIn = onSignIn,
                onSignOut = onSignOut,
                scaffoldState = scaffoldState,
                navController = navController,
            )
        },
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column() {
                if(checkNetworkAvailable()) {
                    if(videoLoading) {
                        LoadingIndicator()
                    } else {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            factory = { context ->
                                // Creates custom view
                                YouTubePlayerView(context).apply {
                                    onSetView(this)
                                    // Sets up listeners for View -> Compose communication
                                    getYouTubePlayerWhenReady(object: YouTubePlayerCallback{
                                        override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                                            youTubePlayer.addListener(YoutubePlaybackListener(onPlayerTimeElapsed))
                                            youTubePlayer.loadVideo(videoId, playerPosition)
                                            youTubePlayer.pause()
                                            onInitPlayer(youTubePlayer)
                                        }
                                    })
                                    lifecycle.addObserver(this)
                                }
                            },
                        )
                        SelectionTabSection(
                            tabIndex = selectedTab,
                            captionsState = captionsState,
                            availableTracks = availableTracks,
                            inspectedCaption = inspectedCaption,
                            inspectedComment = inspectedComment,
                            commentsState = commentsState,
                            onUpdateComments = onUpdateComments,
                            isUpdatingComments = isUpdatingComments,
                            tokens = tokens,
                            tokensMap = tokensMap,
                            selectedWord = selectedWord,
                            onLoadTrack = onLoadTrack,
                            onLoadComments = onLoadComments,
                            onSetSelectedWord = onSetSelectedWord,
                            onChangeTabIndex = onChangeSelectedTab,
                            onAddCommentToFavorites = onShowCommentsDetails,
                            onAddCaptionToFavorites = onShowCaptionsDetails,
                            onSearchWord = onSearchWord,
                            onSetInspectedCaption = onSetInspectedCaption,
                            onSetInspectedComment = onSetInspectedComment,
                            onSetPlayerPosition = onSetPlayerPosition,
                            activeCaption = activeCaption,
                            scope = scope,
                            authState = authState,
                            onRequestLogin = onRequestLogin,
                            navController = navController,
                        )
                    }
                } else {
                    ErrorScreen(
                        text = stringResource(R.string.no_connection_available),
                        subtitle = "¯\\_(ツ)_/¯",
                        action = {
                            onRetry()
                        }
                    )
                }
                if(loadingDialogOpen) {
                    LoadingDialog(
                        text = "Capturing text...",
                        setLoadingDialogOpen = setLoadingDialogOpen,
                    )
                }
                if(sentenceDialogOpen) {
                    SentenceLookupDialog(
                        sentence = recognizedSentence,
                        tokensMap = ocrTokensMap,
                        tokens = ocrTokens,
                        selectedWord = selectedWord,
                        onSetSelectedWord = onSetSelectedWord,
                        onSearchWord = onSearchWord,
                        onSetDialogOpen = setSentenceDialogOpen,
                        onSaveSentence = onSaveSentence,
                        onShowAddedSentenceSnackbar = onShowAddedSentenceSnackbar,
                    )
                }
                if(authPending) {
                    LoadingDialog(text = "Logging in...", setLoadingDialogOpen = {})
                }
            }
            val snackbar = createRef()
            DefaultSnackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(snackbar) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                snackbarHostState = scaffoldState.snackbarHostState,
                onDismiss = {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                }
            )
        }
    }
}

@Composable
private fun CaptionsSection(
    captionsState: DataState<List<YoutubeCaptionModel>>,
    availableTracks: List<YoutubeCaptionTracksModel>,
    inspectedCaption: YoutubeCaptionModel?,
    activeCaption: Int,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    onLoadTrack: (String) -> Unit,
    onAddToFavorites: (String) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSearchWord: () -> Unit,
    navController: NavController,
) {
    when(captionsState) {
        is DataState.Initial<*> ->{
            if(availableTracks.isEmpty()) {
                ErrorScreen(
                    text = stringResource(R.string.no_captions_error),
                    subtitle = "¯\\_(ツ)_/¯"
                )
            } else {
                TracksListSection(tracks = availableTracks, onLoadTrack = onLoadTrack)
            }
        }
        is DataState.Loading -> {
            LoadingIndicator()
        }
        is DataState.Error -> {
            ErrorScreen(
                text = stringResource(R.string.no_captions_for_target_lang_error),
                subtitle = "¯\\_(ツ)_/¯"
            )
        }
        is DataState.Success<List<YoutubeCaptionModel>> -> {
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            LazyColumn(state = listState) {
                val captions = captionsState.data
                items(count = captions.size) { pos ->
                    val isActive = pos == activeCaption
                    val isFocused = captions[pos] == inspectedCaption
                    if(isActive && (!(listState.isItemVisible(pos)) || listState.isLastVisibleItem(pos))) {
                        coroutineScope.launch{
                            listState.animateScrollToItem(pos)
                        }
                    }
                    CaptionsCard(
                        caption = captions[pos],
                        inspectedCaption = inspectedCaption,
                        tokens = tokens,
                        tokensMap = tokensMap,
                        selectedWord = selectedWord,
                        onSetSelectedWord = onSetSelectedWord,
                        isActive = isActive,
                        isFocused = isFocused,
                        onAddToFavorites = onAddToFavorites,
                        onSetPlayerPosition = onSetPlayerPosition,
                        onSearchWord = onSearchWord,
                        onSetInspectedCaption = onSetInspectedCaption,
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
private fun CaptionsCard(
    caption: YoutubeCaptionModel,
    inspectedCaption: YoutubeCaptionModel?,
    tokensMap: Map<Pair<Int, Int>, String>,
    tokens: List<String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    isActive: Boolean,
    isFocused: Boolean,
    onAddToFavorites: (String) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSearchWord: () -> Unit,
    navController: NavController,
) {

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 3.dp),
        backgroundColor =
            if(isActive) Color.LightGray else Color.White,
        border = if(isFocused) BorderStroke(1.dp, MaterialTheme.colors.primary) else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    if(inspectedCaption == caption) {
                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = stringResource(R.string.select_a_word),
                            style = MaterialTheme.typography.body2,
                            color = Color.LightGray,
                        )
                        CustomClickableText(
                            modifier = Modifier.padding(5.dp),
                            textDecoration = TextDecoration.Underline,
                            tokensMap = tokensMap,
                            tokens = tokens,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            selectedToken = selectedWord,
                            onSelectWord = onSetSelectedWord,
                            onClick = { onSearchWord() }
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp, top = 5.dp, bottom = 5.dp, end = 5.dp)
                                .clickable {
                                    onSetInspectedCaption(caption)
                                },
                            text = caption.caption,
                            style = MaterialTheme.typography.body1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            Row(
                modifier = Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 5.dp),
                    onClick = {
                        onAddToFavorites(caption.caption)
                        navController.navigate("sentence_form_screen")
                    }) {
                    Icon(
                        modifier = Modifier
                            .size(22.dp),
                        imageVector = Icons.Rounded.Add,
                        tint = Color.DarkGray,
                        contentDescription = "study"
                    )
                }
                IconButton(
                    modifier = Modifier
                        .size(24.dp),
                    onClick = {
                        onSetPlayerPosition(caption.start ?: 0f)
                    }) {
                    Icon(
                        modifier = Modifier
                            .size(22.dp),
                        imageVector = Icons.Rounded.PlayCircleOutline,
                        tint = Color.DarkGray,
                        contentDescription = "seek"
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentsSection(
    commentsState: DataState<YoutubeCommentsList>,
    inspectedComment: YoutubeCommentModel?,
    onLoadComments: (String?) -> Unit,
    onUpdateComments: () -> Unit,
    isUpdatingComments: Boolean,
    onAddCommentToFavorites: (String) -> Unit,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    onSearchWord: () -> Unit,
    scope: CoroutineScope,
    authState: AuthState<UserModel?>,
    onRequestLogin: () -> Unit,
    navController: NavController,
) {

    val displayedRepliesThread = remember { mutableStateOf(YoutubeCommentModel.Empty()) }
    val displayedRepliesThreadIndex = remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    when(authState) {
        is AuthState.Unauthenticated -> {
            ErrorScreen(
                text = stringResource(R.string.login_to_view_comments),
                action = {
                    onRequestLogin()
                },
                actionName = "Login",
            )
        }
        is AuthState.Authenticated<UserModel?> -> {
            when(commentsState) {
                is DataState.Initial<YoutubeCommentsList> -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        SmallButton(
                            text = stringResource(R.string.load_comments_button),
                            textColor = Color.White,
                            backgroundColor = Blue500,
                            onCLick = {
                                onLoadComments(authState.data?.token)
                            },
                            icon = Icons.Rounded.Downloading,
                            height = 60.dp,
                            width = 100.dp,
                        )
                    }
                }
                is DataState.Loading -> {
                    LoadingIndicator()
                }
                is DataState.Error -> {
                    ErrorScreen(
                        text = stringResource(R.string.fetch_comments_error),
                        subtitle = "Sorry ¯\\_(ツ)_/¯",
                        action = {
                            onLoadComments(authState.data?.token)
                        },
                        actionName = stringResource(R.string.retry),
                    )
                }
                is DataState.Success<YoutubeCommentsList> -> {
                    val comments = commentsState.data.comments
                    /*if(listState.isLastVisibleItem(comments.size -1) && listState.isScrollInProgress) {
                        onUpdateComments()
                    }*/
                    if(displayedRepliesThread.value.isEmpty()) {
                        if(isUpdatingComments) {
                            LoadingIndicator()
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top,
                            ){
                                LazyColumn(
                                    state = listState,
                                ) {
                                    items(
                                        count = comments.size,
                                    ) { pos ->
                                        val isFocused = comments[pos] == inspectedComment
                                        //Log.d("COMMENTSDEBUG", "Current comment: $item")
                                        //Log.d("COMMENTSDEBUG", "Inspected comment: $inspectedComment")
                                        //Log.d("COMMENTSDEBUG", "Is focused: $isFocused")
                                        CommentCard(
                                            comment = comments[pos],
                                            commentPos = pos,
                                            currentDisplayedThread = displayedRepliesThread,
                                            currentDisplayedThreadIndex = displayedRepliesThreadIndex,
                                            isFocused = isFocused,
                                            onAddCommentToFavorites = onAddCommentToFavorites,
                                            tokens = tokens,
                                            tokensMap = tokensMap,
                                            selectedWord = selectedWord,
                                            onSetSelectedWord = onSetSelectedWord,
                                            onSetInspectedComment = onSetInspectedComment,
                                            onSearchWord = onSearchWord,
                                            navController = navController,
                                        )
                                    }
                                    item() {
                                        Row(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                                            IconButton(onClick = {
                                                onUpdateComments()
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Filled.ArrowCircleDown,
                                                    contentDescription = "",
                                                    tint = Color.Black,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        ThreadCommentsSection(
                            inspectedComment = inspectedComment,
                            currentDisplayedThread = displayedRepliesThread,
                            currentDisplayedThreadIndex = displayedRepliesThreadIndex,
                            onAddCommentToFavorites = onAddCommentToFavorites,
                            navController = navController,
                            tokens = tokens,
                            tokensMap = tokensMap,
                            selectedWord = selectedWord,
                            onSetSelectedWord = onSetSelectedWord,
                            onSetInspectedComment = onSetInspectedComment,
                            onSearchWord = onSearchWord,
                            scope = scope,
                            listState = listState,
                        )
                    }
                }
            }
        }
        is AuthState.AuthError -> {
            ErrorScreen(
                text = stringResource(R.string.auth_error_message),
                action = { onRequestLogin() }
            )
        }
    }


}

@Composable
fun ThreadCommentsSection(
    currentDisplayedThread: MutableState<YoutubeCommentModel>,
    currentDisplayedThreadIndex: MutableState<Int>,
    inspectedComment: YoutubeCommentModel?,
    onAddCommentToFavorites: (String) -> Unit,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    onSearchWord: () -> Unit,
    scope: CoroutineScope,
    listState: LazyListState,
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ){
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.Start,
        ){
            IconButton(
                onClick = {
                    scope.launch {
                        listState.scrollToItem(currentDisplayedThreadIndex.value)
                    }
                    currentDisplayedThread.value = YoutubeCommentModel.Empty()
                }
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = "go back",
                    tint = Color.DarkGray,
                )
            }
        }
        val commentsListState = rememberLazyListState()
        LazyColumn(state = commentsListState) {
            val comments = currentDisplayedThread.value.replies
            items(count = comments.size) { pos ->
                val isFocused = comments[pos] == inspectedComment
                CommentCard(
                    comment = comments[pos],
                    commentPos = pos,
                    currentDisplayedThread = currentDisplayedThread,
                    currentDisplayedThreadIndex = currentDisplayedThreadIndex,
                    isFocused = isFocused,
                    onAddCommentToFavorites = onAddCommentToFavorites,
                    tokens = tokens,
                    tokensMap = tokensMap,
                    selectedWord = selectedWord,
                    onSetSelectedWord = onSetSelectedWord,
                    onSetInspectedComment = onSetInspectedComment,
                    onSearchWord = onSearchWord,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun CommentCard(
    comment: YoutubeCommentModel,
    commentPos: Int,
    currentDisplayedThread: MutableState<YoutubeCommentModel>,
    currentDisplayedThreadIndex: MutableState<Int>,
    isFocused: Boolean,
    onAddCommentToFavorites: (String) -> Unit,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    onSearchWord: () -> Unit,
    navController: NavController,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Row (
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            //verticalAlignment = Alignment.CenterVertically,
        ){
            // Profile picture column
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.15f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
            ) {
                CustomAvatar(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    imageUrl = comment.authorProfileImageUrl,
                )
            }
            // Comment Data column
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.65f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
            ) {
                // author + date
                Text(
                    text = comment.authorName, //+ " - " + comment.publishedAt,
                    style = MaterialTheme.typography.body2,
                )
                // Content
                if(isFocused) {
                    CustomClickableText(
                        modifier = Modifier.padding(horizontal = 5.dp),
                        textDecoration = TextDecoration.Underline,
                        tokensMap = tokensMap,
                        tokens = tokens,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        selectedToken = selectedWord,
                        onSelectWord = onSetSelectedWord,
                        onClick = {
                            Log.d("COMMENTSDEBUG", "CLICKED!")
                            onSearchWord()
                        }
                    )
                } else {
                    /*
                    CustomExpandableText(
                        modifier = Modifier.padding(3.dp),
                        text = comment.content,
                        onClickText = { onSetInspectedComment(comment) }
                    ) */
                    Text(
                        modifier = Modifier
                            .padding(3.dp)
                            .clickable {
                                onSetInspectedComment(comment)
                            },
                        text = comment.content,
                    )
                }
                // Buttons Row
                Row(
                    modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Likes
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = Icons.Rounded.ThumbUp,
                                contentDescription = "likes",
                                tint = Color.LightGray,
                            )
                            Text(
                                modifier = Modifier.padding(2.dp),
                                text = comment.likeCount.toString()
                            )
                        }
                        Spacer(Modifier.padding(horizontal = 3.dp))
                        //Dislikes
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ){
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = Icons.Rounded.ThumbDown,
                                contentDescription = "dislikes",
                                tint = Color.LightGray,
                            )
                            val dCounts = comment.dislikesCount ?: 0
                            if(dCounts > 0) {
                                Text(
                                    modifier = Modifier.padding(2.dp),
                                    text = comment.dislikesCount.toString()
                                )
                            }
                        }
                    }

                    // Show Replies Button
                    if(comment.replies.isNotEmpty()) {
                        CustomTextButton(
                            text = stringResource(R.string.comments_show_replies),
                            onClick = {
                                currentDisplayedThread.value = comment
                                currentDisplayedThreadIndex.value = commentPos
                            }
                        )
                    }
                }
            }
            // Add to Favorites Button
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(0.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
            ){
                IconButton(
                    onClick = {
                        onAddCommentToFavorites(comment.content)
                        navController.navigate("sentence_form_screen")
                    }) {
                    Icon(
                        modifier = Modifier
                            .height(28.dp)
                            .width(28.dp),
                        imageVector = Icons.Rounded.Add,
                        tint = Color.DarkGray,
                        contentDescription = "add to favorites"
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionTabSection(
    tabIndex: Int,
    captionsState: DataState<List<YoutubeCaptionModel>>,
    availableTracks: List<YoutubeCaptionTracksModel>,
    inspectedCaption: YoutubeCaptionModel?,
    inspectedComment: YoutubeCommentModel?,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onLoadTrack: (String) -> Unit,
    onLoadComments: (String?) -> Unit,
    onSetSelectedWord: (String) -> Unit,
    commentsState: DataState<YoutubeCommentsList>,
    onUpdateComments: () -> Unit,
    isUpdatingComments: Boolean,
    onChangeTabIndex: (Int) -> Unit,
    onAddCommentToFavorites: (String) -> Unit,
    onAddCaptionToFavorites: (String) -> Unit,
    onSearchWord: () -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    activeCaption: Int,
    scope: CoroutineScope,
    onRequestLogin: () -> Unit,
    authState: AuthState<UserModel?>,
    navController: NavController
) {
    CustomTabMenu(
        tabIndex = tabIndex,
        setTabIndex = { onChangeTabIndex(it) },
        tabHeaders = listOf(
            stringResource(R.string.video_subtitles_tab_header),
            stringResource(R.string.video_comments_tab_header),
        )
    )
    when(tabIndex){
        0 -> {
            CaptionsSection(
                captionsState = captionsState,
                availableTracks = availableTracks,
                inspectedCaption = inspectedCaption,
                activeCaption = activeCaption,
                tokens = tokens,
                tokensMap = tokensMap,
                selectedWord = selectedWord,
                onLoadTrack = onLoadTrack,
                onSetSelectedWord = onSetSelectedWord,
                onAddToFavorites = onAddCaptionToFavorites,
                onSetPlayerPosition = onSetPlayerPosition,
                onSearchWord = onSearchWord,
                onSetInspectedCaption = onSetInspectedCaption,
                navController = navController,
            )
        }
        1 -> {
            CommentsSection(
                commentsState = commentsState,
                inspectedComment = inspectedComment,
                onLoadComments = onLoadComments,
                onUpdateComments = onUpdateComments,
                isUpdatingComments = isUpdatingComments,
                onAddCommentToFavorites = onAddCommentToFavorites,
                navController = navController,
                tokens = tokens,
                tokensMap = tokensMap,
                selectedWord = selectedWord,
                onSetSelectedWord = onSetSelectedWord,
                onSetInspectedComment = onSetInspectedComment,
                onSearchWord = onSearchWord,
                onRequestLogin = onRequestLogin,
                authState = authState,
                scope = scope,
            )
        }
    }
}

@Composable
private fun TracksListSection(
    tracks: List<YoutubeCaptionTracksModel>,
    onLoadTrack: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(vertical=5.dp),
            text = stringResource(R.string.available_tracks),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Start,
        )
        LazyColumn() {
            items(tracks.size) { index ->
                TrackCard(
                    track = tracks[index],
                    onLoadTrack = onLoadTrack,
                )
            }
        }
    }
}

@Composable
private fun TrackCard(
    track: YoutubeCaptionTracksModel,
    onLoadTrack: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        border = BorderStroke(Dp.Hairline, Color.LightGray),
        backgroundColor = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Title and Subtitle
            Column(
                modifier = Modifier
                    .padding(start=24.dp),
                horizontalAlignment = Alignment.Start,

                ) {
                Text(
                    modifier = Modifier.padding(3.dp),
                    text = track.langOriginal,
                    style = MaterialTheme.typography.subtitle1,
                )
                Text(
                    text = track.langTranslated,
                    style = MaterialTheme.typography.body1,
                )
            }
            //CheckBox
            Column(
                modifier = Modifier.padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    onClick = { onLoadTrack(track.langCode) },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = "download track",
                    )
                }
            }
        }
    }
}