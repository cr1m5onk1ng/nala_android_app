package com.example.nala.ui.composables.saved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
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
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(videosData.size) { index ->
                                ItemCard(
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

@Composable
private fun ItemCard(
    item: YoutubeVideoModel,
    onRemoveVideo: (String) -> Unit,
    onSetVideo: (YoutubeVideoModel) -> Unit,
    navController: NavController,
) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    onSetVideo(item)
                    navController.navigate("video_screen")
                },
            elevation = 5.dp,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.5.dp, Color.LightGray),
            backgroundColor = Color.White,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Data Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    // Image Section
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(146.dp),
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(214.dp),
                            contentScale = ContentScale.FillBounds,
                            painter = rememberImagePainter(item.thumbnailUrl),
                            contentDescription = "thumbnail",
                        )
                    }
                    // Content section
                    Row(
                        modifier = Modifier
                            .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ){
                            // Buttons Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(22.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.End,
                            ) {
                                IconButton(
                                    onClick = { onRemoveVideo(item.id) }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(18.dp),
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "remove",
                                        tint = Color.LightGray,
                                    )
                                }
                            }
                            // Title
                            Text(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                text = item.title ?: item.url,
                                style = MaterialTheme.typography.subtitle2,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                            // Domain
                            Text(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                text = "youtube.com",
                                style = MaterialTheme.typography.body2,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }


                        /*
                        Spacer(Modifier.height(5.dp))
                        // Description
                        CustomExpandableText(
                            modifier = Modifier.padding(3.dp),
                            text = item.description ?: "No description provided",
                            style = MaterialTheme.typography.body1,
                            maxLines = 3,
                        ) */
                    }
                }

            }
        }
}
