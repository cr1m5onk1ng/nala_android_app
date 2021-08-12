package com.example.nala.ui.composables.saved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.ui.DataState
import com.example.nala.ui.composables.CustomAvatar
import com.example.nala.ui.composables.CustomExpandableText
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator

@Composable
fun SavedArticlesScreen(
    articles: DataState<List<ArticlesCache>>,
    onRemoveArticle: (String) -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when(articles) {
                is DataState.Initial<*>, DataState.Loading -> {
                    LoadingIndicator()
                }
                is DataState.Error -> {
                    ErrorScreen(text = "Couldn't fetch articles from cache", subtitle = "Sorry dude")
                }
                is DataState.Success<List<ArticlesCache>> -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(articles.data.size) { index ->
                            SavedArticleCardItem(
                                articles.data[index],
                                onRemoveArticle,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedArticleCardItem(
    item: ArticlesCache,
    onRemoveArticle: (String) -> Unit,
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
                    text = item.title,
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
                        onClick = { onRemoveArticle(item.url) }
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
