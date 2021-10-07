package com.example.nala.ui.composables.saved

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.domain.model.yt.YoutubeVideoModel
import com.github.marlonlom.utilities.timeago.TimeAgo

@Composable
fun SavedVideoCard(
    video: YoutubeVideoModel,
    onRemove: (String) -> Unit,
    onSetVideo: (YoutubeVideoModel) -> Unit,
    navController: NavController,
) {
    Card(
        modifier = Modifier
            .clickable(onClick = {
                onSetVideo(video)
                navController.navigate("video_screen")
            })
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            SavedItemImage(imageUrl = video.thumbnailUrl ?: "", Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                SavedItemTitle(video.title ?: "No title provided")
                DomainAndAddTime("youtube.com", video.addedAt ?: "")
            }
            IconButton(
                onClick = { onRemove(video.url) }
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = "remove",
                    tint = Color.LightGray,
                )
            }
        }
    }
}

@Composable
fun SavedArticleCard(
    article: ArticlesCache,
    onRemove: (String) -> Unit,
    onSetArticle: (ArticlesCache) -> Unit,
    navController: NavController,
) {
    Card(
        modifier = Modifier
            .clickable(onClick = {
                onSetArticle(article)
                navController.navigate("article_screen")
            })
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Row (modifier = Modifier.padding(8.dp)){
            SavedItemImage(imageUrl = article.thumbnailUrl ?: "", Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                SavedItemTitle(article.title)
                DomainAndAddTime(article.domain ?: "", TimeAgo.using(article.timeAdded.time))
            }
            IconButton(
                onClick = { onRemove(article.url) }
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = "remove",
                    tint = Color.LightGray,
                )
            }
        }
    }
}

@Composable
fun SavedItemImage(imageUrl: String, modifier: Modifier = Modifier) {
    Image(
        painter = rememberImagePainter(imageUrl),
        contentDescription = null,
        modifier = modifier
            .size(40.dp, 40.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
fun SavedItemTitle(title: String) {
    Text(title, style = MaterialTheme.typography.subtitle1)
}

@Composable
fun DomainAndAddTime(
    domain: String,
    addedAt: String,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = "$domain - $addedAt",
                style = MaterialTheme.typography.body2
            )
        }
    }
}