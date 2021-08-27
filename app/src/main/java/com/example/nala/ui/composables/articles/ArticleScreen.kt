package com.example.nala.ui.composables.articles

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomTopBar
import com.example.nala.ui.theme.Blue500

@Composable
fun ArticleScreen(
    article: String,
    articleLoaded: Boolean,
    isSaved: Boolean,
    onSaveArticle: (String) -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController,
    ) {
        val scope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = "Articles",
                    scope = scope,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = if(isSaved) "Saved" else "Save",
                            color = Color.White,
                        )
                    },
                    onClick = {
                        onSaveArticle(article)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription ="fab save",
                            tint = if(isSaved) Color.Red else Color.White,
                        )
                    },
                    backgroundColor = Blue500,
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