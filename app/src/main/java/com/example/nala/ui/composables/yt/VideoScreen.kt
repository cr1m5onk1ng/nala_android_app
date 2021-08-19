package com.example.nala.ui.composables.yt

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
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.example.nala.domain.model.yt.*
import com.example.nala.network.model.menus.ActionModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import com.example.nala.ui.composables.*
import com.example.nala.ui.composables.menus.CustomTopBar
import com.example.nala.ui.theme.Blue500
import com.example.nala.ui.theme.LightBlue
import com.example.nala.ui.yt.YoutubePlaybackListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
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
    checkVideoSaved: () -> Unit,
    checkNetworkAvailable: () -> Boolean,
    videoLoading: Boolean,
    player: YouTubePlayer?,
    selectedTab: Int,
    captionsState: DataState<List<YoutubeCaptionModel>>,
    commentsState: DataState<YoutubeCommentsList>,
    inspectedCaption: YoutubeCaptionModel?,
    inspectedComment: YoutubeCommentModel?,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    playerPosition: Float,
    onLoadCaptions: () -> Unit,
    onLoadComments: () -> Unit,
    onLoadTrack: (String) -> Unit,
    onAddVideoToFavorites: () -> Unit,
    onRemoveVideoFromFavorites: () -> Unit,
    onInitPlayer: (YouTubePlayer) -> Unit,
    onPlayerTimeElapsed: (Float) -> Unit,
    onClickCaption: (YouTubePlayer, YoutubeCaptionModel) -> Unit,
    onSaveVideo: (YoutubeVideoModel) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onChangeSelectedTab: (Int) -> Unit,
    onShowCaptionsDetails: (String) -> Unit,
    onShowCommentsDetails: (String) -> Unit,
    onSearchWord: () -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    onShowSnackBar: () -> Unit,
    onRetry: () -> Unit,
    activeCaption: Int,
    scaffoldState: ScaffoldState,
    navController: NavController
){
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Videos",
                scope = scope,
                scaffoldState = scaffoldState,
                navController = navController,
                actions = listOf(
                    ActionModel(
                        icon = Icons.Rounded.Favorite,
                        action = {
                            if(!isVideoSaved) {
                                onAddVideoToFavorites()
                                checkVideoSaved()
                            } else {
                                onRemoveVideoFromFavorites()
                                checkVideoSaved()
                            }
                        },
                        isActive = isVideoSaved,
                    )
                )
            )
        },
    ) { paddingValues ->
        ConstraintLayout(modifier = Modifier.padding(paddingValues)) {
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
                            tokens = tokens,
                            tokensMap = tokensMap,
                            selectedWord = selectedWord,
                            onLoadCaptions = onLoadCaptions,
                            onLoadTrack = onLoadTrack,
                            onLoadComments = onLoadComments,
                            onSetSelectedWord = onSetSelectedWord,
                            onChangeTabIndex = onChangeSelectedTab,
                            onAddCommentToFavorites = onShowCommentsDetails,
                            onAddCaptionToFavorites = onShowCaptionsDetails,
                            onSearchWord = onSearchWord,
                            onSetInspectedCaption = onSetInspectedCaption,
                            onSetInspectedComment = onSetInspectedComment,
                            player = player,
                            onSetPlayerPosition = onSetPlayerPosition,
                            activeCaption = activeCaption,
                            navController = navController,
                        )
                    }
                } else {
                    ErrorScreen(
                        text = "Connection not available",
                        subtitle = "¯\\_(ツ)_/¯",
                        action = {
                            onRetry()
                        }
                    )
                }
            }
            val snackbar = createRef()
            DefaultSnackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(snackbar) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
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
    player: YouTubePlayer?,
    onLoadCaptions: () -> Unit,
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
                    text = "No captions available for your target language(s)",
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
            ErrorScreen(text = "No captions found for your target language", subtitle = "¯\\_(ツ)_/¯")
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
                        player = player,
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
    player: YouTubePlayer?,
    onAddToFavorites: (String) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSearchWord: () -> Unit,
    navController: NavController,
) {

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            caption.start?.let {
                player?.seekTo(it)
            }
        },
        shape = MaterialTheme.shapes.large,
        backgroundColor = if(isFocused) LightBlue else if(isActive) Color.LightGray else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(inspectedCaption == caption) {
               CustomClickableText(
                   modifier = Modifier.padding(horizontal = 5.dp),
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
                        .fillMaxWidth(0.7f)
                        .padding(5.dp)
                        .clickable {
                            onSetInspectedCaption(caption)
                        },
                    text = caption.caption,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                modifier = Modifier.fillMaxWidth(0.3f),
                onClick = {
                    onAddToFavorites(caption.caption)
                    onSetPlayerPosition(caption.start ?: 0f)
                    navController.navigate("sentence_form_screen")
                }) {
                Icon(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    imageVector = Icons.Rounded.Add,
                    tint = Color.DarkGray,
                    contentDescription = "search"
                )
            }
        }
    }
}

@Composable
private fun CommentsSection(
    commentsState: DataState<YoutubeCommentsList>,
    inspectedComment: YoutubeCommentModel?,
    onLoadComments: () -> Unit,
    onAddCommentToFavorites: (String) -> Unit,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    player: YouTubePlayer?,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    onSearchWord: () -> Unit,
    navController: NavController,
) {
    when(commentsState) {
        is DataState.Initial<*> -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                SmallButton(
                    text = "Load Comments",
                    textColor = Color.White,
                    backgroundColor = Blue500,
                    onCLick = {
                        onLoadComments()
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
            ErrorScreen(text = "Couldn't fetch comments", subtitle = "Sorry ¯\\_(ツ)_/¯")
        }
        is DataState.Success<YoutubeCommentsList> -> {
            val listState = rememberLazyListState()
            LazyColumn(state = listState) {
                val comments = commentsState.data.comments
                items(count = comments.size) { pos ->
                    val isFocused = comments[pos] == inspectedComment
                    CommentCard(
                        comment = comments[pos],
                        isFocused = isFocused,
                        onAddCommentToFavorites = onAddCommentToFavorites,
                        tokens = tokens,
                        tokensMap = tokensMap,
                        selectedWord = selectedWord,
                        onSetSelectedWord = onSetSelectedWord,
                        player = player,
                        onSetInspectedComment = onSetInspectedComment,
                        onSearchWord = onSearchWord,
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentCard(
    comment: YoutubeCommentModel,
    isFocused: Boolean,
    onAddCommentToFavorites: (String) -> Unit,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    player: YouTubePlayer?,
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
                    .fillMaxHeight()
                    .clickable {
                        onSetInspectedComment(comment)
                    },
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
                        tokensMap = tokensMap,
                        tokens = tokens,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        selectedToken = selectedWord,
                        onSelectWord = onSetSelectedWord,
                        onClick = { onSearchWord() }
                    )
                } else {
                    CustomExpandableText(
                        modifier = Modifier.padding(3.dp),
                        text = comment.content,
                    )
                }
                // Buttons Row
                Row(
                    modifier = Modifier.padding(3.dp),
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
                        /*
                        Modifier
                            .height(40.dp)
                            .width(60.dp), */
                        verticalAlignment = Alignment.CenterVertically,
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
    onLoadCaptions: () -> Unit,
    onLoadTrack: (String) -> Unit,
    onLoadComments: () -> Unit,
    onSetSelectedWord: (String) -> Unit,
    commentsState: DataState<YoutubeCommentsList>,
    onChangeTabIndex: (Int) -> Unit,
    onAddCommentToFavorites: (String) -> Unit,
    onAddCaptionToFavorites: (String) -> Unit,
    onSearchWord: () -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    player: YouTubePlayer?,
    onSetPlayerPosition: (Float) -> Unit,
    activeCaption: Int,
    navController: NavController
) {
    CustomTabMenu(
        tabIndex = tabIndex,
        setTabIndex = { onChangeTabIndex(it) },
        tabHeaders = listOf(
            "SUBTITLES", "COMMENTS"
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
                onLoadCaptions = onLoadCaptions,
                onLoadTrack = onLoadTrack,
                onSetSelectedWord = onSetSelectedWord,
                player = player,
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
                onAddCommentToFavorites = onAddCommentToFavorites,
                navController = navController,
                tokens = tokens,
                tokensMap = tokensMap,
                selectedWord = selectedWord,
                onSetSelectedWord = onSetSelectedWord,
                player = player,
                onSetInspectedComment = onSetInspectedComment,
                onSearchWord = onSearchWord,
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
            text = "Available Tracks",
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