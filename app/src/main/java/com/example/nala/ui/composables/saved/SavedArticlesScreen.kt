package com.example.nala.ui.composables.saved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Remove
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
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.CustomAvatar
import com.example.nala.ui.composables.CustomExpandableText
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomTopBar
import com.example.nala.ui.theme.Blue500

@Composable
fun SavedArticlesScreen(
    articles: DataState<List<ArticlesCache>>,
    onRemoveArticle: (String) -> Unit,
    onSetArticle: (ArticlesCache) -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Articles",
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
                    val articlesData = articles.data
                    if(articlesData.isEmpty()) {
                        ErrorScreen(text = "No articles saved", subtitle = "Add an article by sharing it with the app")
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(articlesData.size) { index ->
                                ItemCard(
                                    articlesData[index],
                                    onRemoveArticle,
                                    onSetArticle = onSetArticle,
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
    item: ArticlesCache,
    onRemoveArticle: (String) -> Unit,
    onSetArticle: (ArticlesCache) -> Unit,
    navController: NavController,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onSetArticle(item)
                navController.navigate("article_screen")
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
                        text = if(item.title.isNotEmpty()) item.title else "No title provided",
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
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(30.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { onRemoveArticle(item.url) }
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
