package com.example.nala.ui.composables.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.BottomBar
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.composables.menus.CustomDrawer
import com.example.nala.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.random.Random


@ExperimentalComposeUiApi
@Composable
fun HomeScreen(
    query: String,
    mightForgetItemsState: DataState<List<WordReviewModel>>,
    onQueryChange: (String) -> Unit,
    onClick: () -> Unit,
    textReceived: Boolean,
    sentenceReceived: Boolean,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
    onMinimize: () -> Unit,
    onCheckPermissions: () -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    if(textReceived){
        onClick()
        navController.navigate("detail_screen")
    }

    if(sentenceReceived){
        navController.navigate("sentence_form_screen")
    }

    Scaffold(
        /*
        topBar = {
            CustomTopBar(scope = scope, scaffoldState = scaffoldState)
        }, */
        bottomBar = {
            BottomBar(
                navController,
                isHomeSelected,
                isReviewsSelected,
                toggleHome,
                toggleReviews
            )
        },

        drawerContent = {
            CustomDrawer(
                modifier = Modifier.background(color = Color.White),
                scope = scope,
                scaffoldState = scaffoldState,
                navController = navController,
            )
        }
    ){ paddingValue ->

        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd),
                    )
                )
                .fillMaxSize()
                .padding(paddingValue),
            verticalArrangement = Arrangement.Top,
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ){
                // OPEN DRAWER BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    horizontalArrangement = Arrangement.Start,
                ){
                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription="drawer",
                            tint = Color.White,
                        )
                    }
                }
                // LOGO SECTION
                Row(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    horizontalArrangement = Arrangement.Start,
                ){
                    Text(
                        text = "NaLa",
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            fontFamily = Quicksand,
                            fontSize = 36.sp,
                            color = LightYellow,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }
            }
            //BODY
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                when(mightForgetItemsState) {
                    is DataState.Initial, DataState.Loading -> {
                        LoadingIndicator()
                    }
                    is DataState.Error -> {
                        Spacer(modifier = Modifier.padding(top = 64.dp))
                    }
                    is DataState.Success<List<WordReviewModel>> -> {
                        Text(
                            "Words you might forget",
                            modifier = Modifier
                                .padding(16.dp),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                fontFamily = Quicksand,
                                color = Color.White
                            )
                        )
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            horizontalArrangement = Arrangement.Center
                        ){
                            items(mightForgetItemsState.data.size) { index ->
                                val word = mightForgetItemsState.data[index].word
                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .alpha(0.9F)
                                        .clickable {
                                            onQueryChange(word)
                                            onClick()
                                            navController.navigate("detail_screen")
                                        },
                                    backgroundColor = Blue400,
                                    shape = RoundedCornerShape(18.dp),
                                    contentColor = Blue500,
                                    elevation = 3.dp,
                                ) {
                                    Text(
                                        word,
                                        modifier = Modifier.padding(8.dp),
                                        style = TextStyle(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Light,
                                            color = TEXT_COLORS[Random.nextInt(0, TEXT_COLORS.size)]
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(
                    modifier = Modifier.height(10.dp),
                )
                TextField(
                    value = query,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .alpha(0.6f),
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 20.sp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Blue400,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent),
                    shape = RoundedCornerShape(32.dp),
                    placeholder = { Text("Search in dictionary") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "search",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if(query.isNotEmpty())
                            IconButton(
                                onClick = {
                                    onQueryChange("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "reset",
                                    tint = Color.White
                                )
                            }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions (
                        onSearch = {
                            onClick()
                            keyboardController?.hide()
                            navController.navigate("detail_screen")
                        }
                    )
                )
            }
        }
    }
}