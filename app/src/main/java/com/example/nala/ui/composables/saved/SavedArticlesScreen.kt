package com.example.nala.ui.composables.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.domain.model.auth.UserModel
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomDrawer
import com.example.nala.ui.composables.menus.CustomTopBar
import com.example.nala.ui.theme.Blue500
import com.example.nala.ui.theme.GreyBackground
import com.example.nala.ui.theme.GreyBackgroundVariant

@Composable
fun SavedArticlesScreen(
    articles: DataState<List<ArticlesCache>>,
    onRemoveArticle: (String) -> Unit,
    onSetArticle: (ArticlesCache) -> Unit,
    authState: AuthState<UserModel?>,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        backgroundColor = Color.White,
        scaffoldState = scaffoldState,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.saved_articles_header),
                backgroundColor = MaterialTheme.colors.primary,
                scope = scope,
                scaffoldState = scaffoldState,
                navController = navController
            )
        },
        drawerContent = {
            CustomDrawer(
                modifier = Modifier.background(color = Color.White),
                scope = scope,
                authState = authState,
                onSignIn = onSignIn,
                onSignOut = onSignOut,
                scaffoldState = scaffoldState,
                navController = navController,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(articlesData.size) { index ->
                                SavedArticleCard(
                                    article = articlesData[index],
                                    onRemove = onRemoveArticle,
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




