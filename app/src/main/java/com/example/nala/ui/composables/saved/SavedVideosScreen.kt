package com.example.nala.ui.composables.saved

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
import com.example.nala.domain.model.auth.UserModel
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.domain.model.yt.YoutubeVideoModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.DefaultSnackbar
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomDrawer
import com.example.nala.ui.composables.menus.CustomTopBar

@ExperimentalCoilApi
@Composable
fun SavedVideosScreen(
    videos: DataState<List<YoutubeVideoModel>>,
    onRemoveVideo: (String) -> Unit,
    onSetVideo: (YoutubeVideoModel) -> Unit,
    onRestoreVideo: (YoutubeVideoModel) -> Unit,
    scaffoldState: ScaffoldState,
    authState: AuthState<UserModel?>,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    showSnackbar: (ScaffoldState) -> Unit,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val removedItem = remember{ mutableStateOf<YoutubeVideoModel?>(null) }
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.White,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.saved_videos_header),
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
        ConstraintLayout(modifier = Modifier.padding(paddingValues)) {
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
                        ErrorScreen(
                            text = stringResource(R.string.saved_videos_fetch_error),
                            subtitle = "sorry dude"
                        )
                    }
                    is DataState.Success<List<YoutubeVideoModel>> -> {
                        val videosData = videos.data
                        if(videosData.isEmpty()) {
                            ErrorScreen(
                                text = stringResource(R.string.no_video_saved_error),
                                subtitle = stringResource(R.string.no_video_saved_error_subtitle),
                            )
                        } else {
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                items(videosData.size) { index ->
                                    SavedVideoCard(
                                        video = videosData[index],
                                        onRemove = onRemoveVideo,
                                        onSetVideo = onSetVideo,
                                        onSetRemovedItem = { removedItem.value = it },
                                        navController = navController,
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
                    removedItem.value?.let{
                        onRestoreVideo(it)
                    }
                }
            )
        }
    }
}


