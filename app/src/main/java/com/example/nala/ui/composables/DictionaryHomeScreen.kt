package com.example.nala.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
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
import com.example.nala.R
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.ui.theme.*
import kotlin.random.Random


@ExperimentalComposeUiApi
@Composable
fun HomeScreen(
    query: String,
    mightForgetItems: List<WordReviewModel>,
    mightForgetItemsLoaded: Boolean,
    onQueryChange: (String) -> Unit,
    onClick: () -> Unit,
    textReceived: Boolean,
    sentenceReceived: Boolean,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
    navController: NavController
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    if(textReceived){
        onClick()
        navController.navigate("detail_screen")
    }

    if(sentenceReceived){
        navController.navigate("sentence_form_screen")
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                navController,
                isHomeSelected,
                isReviewsSelected,
                toggleHome,
                toggleReviews
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(!mightForgetItemsLoaded) {
                LoadingIndicator()
            }
            else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "NaLa",
                        modifier = Modifier.padding(top = 45.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = Quicksand,
                            fontSize = 42.sp,
                            color = LightYellow,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                    Spacer(modifier = Modifier.height(40.dp))
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
                        items(mightForgetItems.size) { index ->
                            val word = mightForgetItems[index].word
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .alpha(0.9F)
                                    .clickable {
                                        onQueryChange(word)
                                        onClick()
                                        onQueryChange("")
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
                                Icons.Rounded.Search,
                                contentDescription = "search",
                                tint = Color.White )

                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search,
                        ),
                        keyboardActions = KeyboardActions (
                            onSearch = {
                                onClick()
                                onQueryChange("")
                                keyboardController?.hide()
                                navController.navigate("detail_screen")
                            }
                        )
                    )
                }
            }
        }

    }
}
