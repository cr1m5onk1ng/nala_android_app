package com.example.nala.ui.composables

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
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.ui.theme.Blue700
import com.example.nala.ui.theme.LightBlue
import com.example.nala.ui.theme.Quicksand

@Composable
fun ReviewListScreen(
    isLoading: Boolean,
    reviewItems: List<DictionaryModel>,
    setStudyItem: (DictionaryModel) -> Unit,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
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
        if(reviewItems.isEmpty()) {
            ErrorScreen(text = "No items added to review", subtitle = "")
        }
        else {
            Column(
                modifier = Modifier
                    .background(
                        color = LightBlue
                    )
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    BackButton(navController)
                    Text(
                        "Reviews",
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(
                            fontFamily = Quicksand,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    items(count = reviewItems.size) { index ->
                        ReviewCard(reviewItems[index], setStudyItem, navController)
                    }
                }
            }
        }
    }

}

@Composable
fun ReviewCard(
    item: DictionaryModel,
    setStudyItem: (DictionaryModel) -> Unit,
    navController: NavController,
    ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable{
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Text Section
            Text(
                item.data?.first()?.slug ?: "",
                modifier = Modifier
                    .padding(start = 26.dp),
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontWeight = FontWeight.W500,
                    fontSize = 32.sp,
                    color = Color.Black
                )
            )
            // Action Button
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = "study",
                    tint = Blue700
                )


            }
        }
    }
}
