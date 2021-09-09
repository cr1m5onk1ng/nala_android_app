package com.example.nala.ui.composables.articles

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.network.model.menus.ActionModel
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomTopBar

@Composable
fun ArticleScreen(
    article: String,
    articleLoaded: Boolean,
    isSaved: Boolean,
    onSaveArticle: () -> Unit,
    onRemoveArticle: () -> Unit,
    onSetIsArticleSaved: (Boolean) -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController,
    ) {
        val scope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = stringResource(R.string.article_screen_header),
                    scope = scope,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    actions = listOf(
                        ActionModel(
                            icon = Icons.Rounded.Favorite,
                            action = {
                                if(isSaved) {
                                    onRemoveArticle()
                                    onSetIsArticleSaved(false)
                                } else {
                                    onSaveArticle()
                                    onSetIsArticleSaved(true)
                                }
                            },
                            isActive = isSaved,
                        )
                    )
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                if(!articleLoaded) {
                    LoadingIndicator()
                } else {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        factory = { context ->
                            // Creates custom view
                            WebView(context).apply{
                                webViewClient = WebViewClient()
                                loadUrl(article)
                                settings.javaScriptEnabled = true
                            }
                        },
                    )
                }
            }
        }
}