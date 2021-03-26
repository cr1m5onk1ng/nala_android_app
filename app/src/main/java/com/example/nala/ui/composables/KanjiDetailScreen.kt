package com.example.nala.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.ui.theme.Quicksand

@Composable
fun KanjiDetailScreen(
    kanji: KanjiModel,
    story: String,
    navController: NavController,
) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ){
        item() {
            Column() {
                var tags: MutableList<String> = mutableListOf()
                var freq = kanji?.freq ?: ""
                if(freq.isNotEmpty()) {
                    freq = "frequency: $freq"
                    tags.add(freq)
                }

                var jlpt = kanji?.jlpt ?: ""
                if(jlpt.isNotEmpty()) {
                    jlpt = "jlptn-$jlpt"
                    tags.add(jlpt)
                }

                var grade = kanji?.grade ?: ""
                if(grade.isNotEmpty()) {
                    grade = "grade: $grade"
                    tags.add(grade)
                }

                // SECTIONS
                BackButton(navController)
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                KanjiSection(kanji = kanji.kanji)
                TagRow(tags = tags)
                StorySection(story = story)
                DetailsSection(kanji)
            }
        }
    }
}

@Composable
fun KanjiSection(
    kanji: String
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            kanji,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun StorySection(
    story: String
){
    Column (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        3.dp,
                        color = Color.LightGray
                    )
                )
                .padding(vertical = 8.dp)
        )
        Text(
            story,
            style = TextStyle(
                fontFamily = Quicksand,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp,
                color = Color.Black
            )
        )
        Spacer(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        3.dp,
                        color = Color.LightGray
                    )
                )
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun DetailsColumn(
    details: List<String>,
    title: String,
    fontSize: TextUnit
) {
    Column(
        modifier = Modifier
            .padding(3.dp)
    ){
        Text(
            title,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        )
        for (detail in details) {
            Text(
                detail,
                modifier = Modifier
                    .padding(vertical = 8.dp),
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontSize = fontSize,
                    fontWeight = FontWeight.W500,
                    color = Color.Black,
                ),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DetailsSection(
    kanji: KanjiModel
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DetailsColumn(details = kanji?.meaning ?: listOf(), title = "Meanings", 20.sp)
        DetailsColumn(details = kanji?.reading?.kun ?: listOf(), title = "Kun", 16.sp)
        DetailsColumn(details = kanji?.reading?.on ?: listOf(), title = "On", 16.sp)
    }
}
