package com.example.nala.ui.composables.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.domain.model.yt.YoutubeVideoModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomTopBar
import com.example.nala.ui.theme.Blue500

@Composable
fun SavedVideosScreen(
    videos: DataState<List<YoutubeVideoModel>>,
    onRemoveVideo: (String) -> Unit,
    onSetVideo: (YoutubeVideoModel) -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        backgroundColor = Color.White,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.saved_videos_header),
                backgroundColor = Blue500,
                contentColor = Color.White,
                navIcon = Icons.Rounded.ArrowBack,
                navIconAction = {
                    navController.popBackStack()
                },
                scope = scope,
                scaffoldState = scaffoldState,
                navController = navController
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when(videos) {
                is DataState.Initial<*>, DataState.Loading -> {
                    LoadingIndicator()
                }
                is DataState.Error -> {
                    ErrorScreen(text = stringResource(R.string.saved_videos_fetch_error), subtitle = "sorry dude")
                }
                is DataState.Success<List<YoutubeVideoModel>> -> {
                    val videosData = videos.data
                    if(videosData.isEmpty()) {
                        ErrorScreen(
                            text = stringResource(R.string.no_video_saved_error),
                            subtitle = stringResource(R.string.no_video_saved_error_subtitle),
                        )
                    } else {
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(videosData.size) { index ->
                                SavedVideoCard(
                                    videosData[index],
                                    onRemoveVideo,
                                    onSetVideo = onSetVideo,
                                    navController = navController,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


