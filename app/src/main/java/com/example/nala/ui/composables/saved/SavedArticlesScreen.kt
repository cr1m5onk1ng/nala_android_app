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
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.domain.model.utils.DataState
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
                title = stringResource(R.string.saved_articles_header),
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
                    ErrorScreen(text = stringResource(R.string.articles_fetch_error), subtitle = "Sorry dude")
                }
                is DataState.Success<List<ArticlesCache>> -> {
                    val articlesData = articles.data
                    if(articlesData.isEmpty()) {
                        ErrorScreen(
                            text = stringResource(R.string.no_articles_saved_error),
                            subtitle = stringResource(R.string.no_articles_saved_error_subtitle),
                        )
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(128.dp)
            .clickable {
                onSetArticle(item)
                navController.navigate("article_screen")
            },
        elevation = 5.dp,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Color.LightGray),
        backgroundColor = Color.White,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Data Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
                        .fillMaxSize()
                        .padding(start = 5.dp, top = 5.dp, bottom = 5.dp),
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
                                onClick = { onRemoveArticle(item.url) }
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
                            text = if(item.title.isNotEmpty()) item.title else item.url,
                            style = MaterialTheme.typography.subtitle2,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                        // Domain
                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = item.domain ?: "unknown domain",
                            style = MaterialTheme.typography.body2,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

        }
    }
}


