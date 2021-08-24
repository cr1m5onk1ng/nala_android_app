package com.example.nala.ui.composables.saved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nala.domain.model.yt.YoutubeVideoModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.CustomAvatar
import com.example.nala.ui.composables.CustomExpandableText
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
                title = "Videos",
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
                    ErrorScreen(text = "Couldn't fetch videos from cache", subtitle = "sorry dude")
                }
                is DataState.Success<List<YoutubeVideoModel>> -> {
                    val videosData = videos.data
                    if(videosData.isEmpty()) {
                        ErrorScreen(text = "No videos saved", subtitle = "Import a video by sharing it with the app")
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
                .padding(16.dp)
                .clickable {
                    onSetVideo(item)
                    navController.navigate("video_screen")
                },
            elevation = 5.dp,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.5.dp, Color.LightGray),
            backgroundColor = Color.White,
        ) {
            Column() {
                // Data Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Image Section
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(146.dp),
                    ) {
                        Image(
                            modifier = Modifier
                                .height(128.dp)
                                .width(214.dp),
                            contentScale = ContentScale.FillBounds,
                            painter = rememberImagePainter(item.thumbnailUrl),
                            contentDescription = "thumbnail",
                        )
                    }
                    // Content section
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {

                        // Title
                        Text(
                            text = item.title ?: "No title provided",
                            style = MaterialTheme.typography.body2,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                        // Buttons Row
                        Row(
                            modifier = Modifier
                                .padding(bottom = 1.dp, end = 1.dp)
                                .height(20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            IconButton(
                                onClick = { onRemoveVideo(item.id) }
                            ) {
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = "remove",
                                    tint = Color.LightGray,
                                )
                            }
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

@Composable
private fun SavedVideoItemCard(
    item: YoutubeVideoModel,
    onRemoveVideo: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 5.dp,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Color.LightGray),
        backgroundColor = Color.White,
    ) {
        Row (
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            // Video Thumbnail Column
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight().background(Color.Blue),
                verticalArrangement = Arrangement.Top,
            ) {
                CustomAvatar(
                    modifier = Modifier
                        .size(50.dp),
                    imageUrl = item.thumbnailUrl,
                )
            }
            // Video Data column
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.7f).background(Color.Red)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
            ) {
                // Title
                Text(
                    text = item.title ?: "No title provided",
                    style = MaterialTheme.typography.body1,
                )
                // Description
                item.description?.let{
                    CustomExpandableText(
                        modifier = Modifier.padding(3.dp),
                        text = item.description,
                    )
                }
                // Buttons Row
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Remove Button
                    IconButton(
                        onClick = { onRemoveVideo(item.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Remove,
                            contentDescription = "remove",
                            tint = Color.LightGray,
                        )
                    }
                }
            }
        }
    }
}