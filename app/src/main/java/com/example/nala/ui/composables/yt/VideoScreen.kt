package com.example.nala.ui.composables.yt

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.example.nala.domain.model.yt.YoutubeCaptionModel
import com.example.nala.ui.DataState
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.yt.YoutubePlaybackListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LazyListState.isLastVisibleItem(index: Int) =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == index


@Composable
fun VideoScreen(
    lifecycle: Lifecycle,
    videoId: String,
    captionsState: DataState<List<YoutubeCaptionModel>>,
    playerPosition: Float,
    onPlayerTimeElapsed: (Float) -> Unit,
    onClickCaption: (YouTubePlayer, YoutubeCaptionModel) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    onAddToFavorites: (String) -> Unit,
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
                                player = youTubePlayer
                            }
                        })
                        lifecycle.addObserver(this)
                        setOnClickListener {

                        }
                    }
                },
            )
            when(captionsState) {
                is DataState.Initial<*>, DataState.Loading -> {
                    LoadingIndicator()
                }
                is DataState.Error -> {
                    ErrorScreen(text = "Can't Fetch Data", "Sorry about that")
                }
                is DataState.Success<List<YoutubeCaptionModel>> -> {
                    val listState = rememberLazyListState()
                    // Remember a CoroutineScope to be able to launch
                    val coroutineScope = rememberCoroutineScope()
                    LazyColumn(state = listState) {
                        val captions = captionsState.data
                        items(count = captions.size) { pos ->
                            val isActive = pos == activeCaption
                            if(isActive && listState.isLastVisibleItem(pos)) {
                                coroutineScope.launch{
                                    listState.animateScrollToItem(pos)
                                }
                            }
                            /*
                            if(isActive && pos % 4 == 0){
                                coroutineScope.launch{
                                    listState.animateScrollToItem(pos)
                                }
                            } */
                            CaptionsCard(
                                caption = captions[pos],
                                isActive = isActive,
                                player = player,
                                onAddToFavorites = onAddToFavorites,
                                onSetPlayerPosition = onSetPlayerPosition,
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaptionsCard(
    caption: YoutubeCaptionModel,
    isActive: Boolean,
    player: YouTubePlayer?,
    onAddToFavorites: (String) -> Unit,
    onSetPlayerPosition: (Float) -> Unit,
    navController: NavController,
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            if (player == null) {
                Log.d("YOUTUBEDEBUG", "PLAYER IS NULL")
            } else {
                player.seekTo(caption.start ?: 0f)
            }

        },
        shape = MaterialTheme.shapes.large,
        backgroundColor = if(isActive) Color.LightGray else Color.White
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(5.dp),
                text = caption.caption,
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(
                modifier = Modifier.fillMaxWidth(0.3f),
                onClick = {
                    onAddToFavorites(caption.caption)
                    onSetPlayerPosition(caption.start ?: 0f)
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