package com.example.nala.ui.composables.saved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.nala.domain.model.yt.YoutubeVideoModel
import com.example.nala.ui.DataState
import com.example.nala.ui.composables.CustomAvatar
import com.example.nala.ui.composables.CustomExpandableText
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator

@Composable
fun SavedVideosScreen(
    videos: DataState<List<YoutubeVideoModel>>,
    onRemoveVideo: (String) -> Unit,
) {
    Scaffold { paddingValues ->
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
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(videos.data.size) { index ->
                            ItemCard(
                                videos.data[index],
                                onRemoveVideo,
                            )
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
            Column() {
                // Data Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Image Section
                    Column(
                        /*
                        modifier = Modifier
                            .background(Color.Red), */
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .height(128.dp)
                                .width(128.dp),
                                //.background(Color.Green),
                            contentScale = ContentScale.FillBounds,
                            painter = rememberImagePainter(item.thumbnailUrl),
                            contentDescription = "thumbnail",
                        )
                    }
                    // Content section
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                            //.background(Color.Blue),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Title
                        Text(
                            text = item.title ?: "No title provided",
                            style = MaterialTheme.typography.body1,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(5.dp))
                        // Description
                        CustomExpandableText(
                            modifier = Modifier.padding(3.dp),
                            text = item.description ?: "No description provided",
                            style = MaterialTheme.typography.subtitle1,
                            maxLines = 3,
                        )
                    }
                }
                // Buttons Row
                Row(
                    modifier = Modifier.padding(5.dp).fillMaxWidth().height(30.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { onRemoveVideo(item.id) }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "remove",
                            tint = Color.LightGray,
                        )
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
                    .fillMaxWidth(0.3f)
                    .fillMaxHeight(),
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
                    .fillMaxWidth(0.7f),
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