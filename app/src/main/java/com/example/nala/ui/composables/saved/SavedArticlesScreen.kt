package com.example.nala.ui.composables.saved

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.example.nala.R
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.domain.model.auth.UserModel
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.DefaultSnackbar
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomDrawer
import com.example.nala.ui.composables.menus.CustomTopBar

@ExperimentalCoilApi
@Composable
fun SavedArticlesScreen(
    articles: DataState<List<ArticlesCache>>,
    onRemoveArticle: (String) -> Unit,
    onSetArticle: (ArticlesCache) -> Unit,
    onRestoreArticle: (ArticlesCache) -> Unit,
    authState: AuthState<UserModel?>,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController,
    showSnackbar: (ScaffoldState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val removedItem = remember { mutableStateOf<ArticlesCache?>(null) }
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
        ConstraintLayout(
            modifier = Modifier.padding(paddingValues)
        ){
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
                                        onSetRemovedItem = {
                                            removedItem.value = it
                                            Log.d("FAVORITESDEBUG", "Removed Item: ${removedItem.value}")
                                                           },
                                        scaffoldState = scaffoldState,
                                        onShowSnackbar = showSnackbar,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            val snackbar = createRef()
            DefaultSnackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(snackbar) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                snackbarHostState = scaffoldState.snackbarHostState,
                onDismiss = {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                },
                onAction = {
                    Log.d("FAVORITESDEBUG", "Restoring Item: ${removedItem.value}")
                    removedItem.value?.let{
                        onRestoreArticle(it)
                    }
                }
            )
        }
    }
}




