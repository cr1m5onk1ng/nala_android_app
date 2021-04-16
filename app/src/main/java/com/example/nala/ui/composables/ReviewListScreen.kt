package com.example.nala.ui.composables

import android.R.attr
import android.widget.CheckBox
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.ui.theme.Blue700
import com.example.nala.ui.theme.LightBlue
import com.example.nala.ui.theme.Quicksand
import android.R.attr.checked
import android.R.attr.fontFamily
import android.graphics.Paint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration


@Composable
fun ReviewListScreen(
    isLoading: Boolean,
    reviewItems: List<WordReviewModelDto>,
    setStudyItem: (WordReviewModelDto) -> Unit,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
    updateReviewItem: (quality: Int, reviewItem: WordReviewModelDto) -> Unit,
    navController: NavController
) {
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
    ) {
        if (isLoading) {
            LoadingIndicator()
        }
        else if(reviewItems.isEmpty()) {
            ErrorScreen(text = "No items added to review", subtitle = "")
        }
        else {
            Column(
                modifier = Modifier
                    .background(
                        color = LightBlue
                    )
                    .fillMaxWidth()
                    .height(
                        710.dp,
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BackButton(navController)
                    Text(
                        "Reviews",
                        style = TextStyle(
                            fontFamily = Quicksand,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(count = reviewItems.size) { index ->
                        ReviewCard(
                            reviewItems[index],
                            setStudyItem,
                            updateReviewItem,
                            navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    item: WordReviewModelDto,
    setStudyItem: (WordReviewModelDto) -> Unit,
    updateReviewItem: (quality: Int, reviewItem: WordReviewModelDto) -> Unit,
    navController: NavController,
    ) {
    val isOkChecked = remember { mutableStateOf(false) }
    val isKoChecked = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable {
                setStudyItem(item)
                navController.navigate(R.id.show_review_detail)
            },
        backgroundColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray,
        ),
        elevation = 5.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TEXT SECTION
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(180.dp)
            ) {
                // Reading
                Text(
                    item.reading,
                    modifier = Modifier.padding(bottom=3.dp),
                    style = TextStyle(
                        fontFamily = Quicksand,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                    )
                )
                // Word
                Text(
                    item.word,
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = TextStyle(
                        fontFamily = Quicksand,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.W500,
                    )
                )
                //CHECKBOX ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // CHECKBOX
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ){
                        CustomCheckBox(
                            label = "Ok",
                            isChecked = isOkChecked,
                            onCheckedChange = {
                                if(it) {
                                    isOkChecked.value = true
                                    isKoChecked.value = false
                                }
                                else{
                                    isOkChecked.value = false
                                }
                            },
                            colors = CheckboxDefaults.colors(Color.Green)
                        )
                        CustomCheckBox(
                            label = "Ko",
                            isChecked = isKoChecked,
                            onCheckedChange = {
                                if(it) {
                                    isKoChecked.value = true
                                    isOkChecked.value = false
                                }
                                else{
                                    isKoChecked.value = false
                                }
                            },
                            colors = CheckboxDefaults.colors(Color.Red)
                        )
                    }
                }
            }
            // DISMISS BUTTON
            Column(
                verticalArrangement = Arrangement.Center,
                //modifier = Modifier.width(80.dp)
            ) {
                TextButton(
                    onClick = {
                        val quality =
                            if(isOkChecked.value) 4 else 2
                        updateReviewItem(quality, item)
                    },
                ){
                    Text(
                        "Dismiss",
                        style = TextStyle(
                            fontFamily = Quicksand,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            color = Blue700,
                            textDecoration = TextDecoration.Underline
                        ),
                    )
                }
            }
            //BUTTONS SECTION

        }
    }
}

@Composable
fun CustomCheckBox(
    label: String,
    isChecked: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    colors: CheckboxColors
) {
    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = onCheckedChange,
            enabled = true,
            colors = colors
        )
        Text(text = label)
    }
}

