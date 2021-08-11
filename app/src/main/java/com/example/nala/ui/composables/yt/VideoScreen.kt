package com.example.nala.ui.composables.yt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.example.nala.domain.model.yt.YoutubeCaptionModel
import com.example.nala.domain.model.yt.YoutubeCommentModel
import com.example.nala.domain.model.yt.YoutubeCommentsList
import com.example.nala.ui.DataState
import com.example.nala.ui.composables.*
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
    onInitPlayer: (YouTubePlayer) -> Unit,
    onPlayerTimeElapsed: (Float) -> Unit,
    onClickCaption: (YouTubePlayer, YoutubeCaptionModel) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onChangeSelectedTab: (Int) -> Unit,
    onAddCaptionToFavorites: (String) -> Unit,
    onAddCommentToFavorites: (String) -> Unit,
    onSearchWord: () -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSetInspectedComment: (YoutubeCommentModel) -> Unit,
    activeCaption: Int,
    navController: NavController
){

    var player: YouTubePlayer? = null

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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
                                onInitPlayer(youTubePlayer)
                            }
                        })
                        lifecycle.addObserver(this)
                        setOnClickListener {

                        }
                    }
                },
            )
            SelectionTabSection(
                tabIndex = selectedTab,
                captionsState = captionsState,
                inspectedCaption = inspectedCaption,
                inspectedComment = inspectedComment,
                commentsState = commentsState,
                tokens = tokens,
                tokensMap = tokensMap,
                selectedWord = selectedWord,
                onSetSelectedWord = onSetSelectedWord,
                onChangeTabIndex = onChangeSelectedTab,
                onAddCommentToFavorites = onAddCommentToFavorites,
                onAddCaptionToFavorites = onAddCaptionToFavorites,
                onSearchWord = onSearchWord,
                onSetInspectedCaption = onSetInspectedCaption,
                onSetInspectedComment = onSetInspectedComment,
                player = player,
                onSetPlayerPosition = onSetPlayerPosition,
                activeCaption = activeCaption,
                navController = navController,
            )
        }
    }
}

@Composable
fun CaptionsSection(
    captionsState: DataState<List<YoutubeCaptionModel>>,
    inspectedCaption: YoutubeCaptionModel?,
    activeCaption: Int,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    player: YouTubePlayer?,
    onAddToFavorites: (String) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onSetInspectedCaption: (YoutubeCaptionModel) -> Unit,
    onSearchWord: () -> Unit,
    navController: NavController,
) {
    when(captionsState) {
        is DataState.Initial<*>, DataState.Loading -> {
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
fun CaptionsCard(
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
fun CommentsSection(
    commentsState: DataState<YoutubeCommentsList>,
    inspectedComment: YoutubeCommentModel?,
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
        is DataState.Initial<*>, DataState.Loading -> {
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
fun CommentCard(
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
            /*.clickable {
                onAddCommentToFavorites(comment.content)
                navController.navigate("sentence_form_screen")
            }, */
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Row (
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ){
            // Profile picture column
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.2f),
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
                    .fillMaxWidth(0.8f)
                    .clickable{
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
                        if(comment.dislikesCount > 0) {
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
                modifier = Modifier.padding(2.dp).fillMaxWidth(0.2f),
                verticalArrangement = Arrangement.Center
            ){
                IconButton(
                    onClick = {
                        onAddCommentToFavorites(comment.content)
                        navController.navigate("sentence_form_screen")
                    }) {
                    Icon(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
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
fun SelectionTabSection(
    tabIndex: Int,
    captionsState: DataState<List<YoutubeCaptionModel>>,
    inspectedCaption: YoutubeCaptionModel?,
    inspectedComment: YoutubeCommentModel?,
    tokens: List<String>,
    tokensMap: Map<Pair<Int, Int>, String>,
    selectedWord: String,
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
                inspectedCaption = inspectedCaption,
                activeCaption = activeCaption,
                tokens = tokens,
                tokensMap = tokensMap,
                selectedWord = selectedWord,
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