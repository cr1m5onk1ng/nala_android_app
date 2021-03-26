package com.example.nala.ui.composables

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
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
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
import com.example.nala.R
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.ui.theme.*
import kotlin.random.Random


@ExperimentalComposeUiApi
@Composable
fun HomeScreen(
    query: String,
    mightForgetItems: List<DictionaryModel>,
    onQueryChange: (String) -> Unit,
    onClick: () -> Unit,
    textReceived: Boolean,
    unsetSharedText: () -> Unit,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
    navController: NavController
) {
    if(textReceived){
        onClick()
        navController.navigate(R.id.show_details)
        unsetSharedText()
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
    ){
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd),
                    )
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "NaLa",
                    modifier = Modifier.padding(top = 32.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = Quicksand,
                        fontSize = 42.sp,
                        color = LightYellow,
                        fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(modifier = Modifier.height(60.dp))
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    items(mightForgetItems.size) { index ->
                        val word = mightForgetItems[index].data?.first()?.japanese?.first()?.word ?: ""
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(color = Color.Transparent)
                                .alpha(0.9F)
                                .clickable {
                                    onQueryChange(word)
                                    onClick()
                                    navController.navigate(R.id.show_details)
                                },
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
                    modifier = Modifier.height(50.dp),
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ){
                    val keyboardController = LocalSoftwareKeyboardController.current
                    TextField(
                        value = query,
                        modifier = Modifier.alpha(0.6f),
                        onValueChange = onQueryChange,
                        textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 20.sp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor =  Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent),
                        shape = RoundedCornerShape(18.dp),
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
                                keyboardController?.hideSoftwareKeyboard()
                                navController.navigate(R.id.show_details)
                            }
                        )
                    )
                }
            }
        }

    }
}
