package com.example.nala.ui.composables

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.domain.model.dictionary.Data
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.ui.theme.*

val specialStyle = SpanStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 26.sp,
    fontFamily = Quicksand,
    color = Color.Black,
    textDecoration = TextDecoration.Underline,
)
val normalStyle = SpanStyle(
    fontWeight = FontWeight.Light,
    fontSize = 24.sp,
    fontFamily = Quicksand,
    color = Color.Black,
)

@Composable
fun StudyScreen(
    context: String,
    wordModel: DictionaryModel,
    kanjiDict: KanjiCollection,
    navController: NavController,
    contextLoading: Boolean,
    wordLoading: Boolean,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
) {

    val sentences = listOf(
        "殺人 略奪 治安維持も無く力は力でしか抗えない犯罪の5割はアンドロイド\n",
        "僕らが信じる真実は誰かの創作かもしれない僕らが見てるこの世界は\n",
        "風がそよぎ 海が凪ぎ空に虫と鳥が戯れる木々は今青々と\n",
        "説教じみた話じゃつまらない分かってるだからこそ感じて経験は何よりも饒舌そしてそれを忘れちゃいけないよ\n",
        "どう? 理解できたかなこれが人類の原風景上映はこれにて終了です\n",
    )

    Scaffold(

    ) {
        if(contextLoading || wordLoading){
            LoadingIndicator()
        }
        else if(wordModel.data.isEmpty()) {
            ErrorScreen(
                text = "Target word not found in Jisho dictionary",
                subtitle = "¯\\_(ツ)_/¯"
            )
        }
        else {
            val word = wordModel.data?.first().slug ?: ""
            val reading = wordModel.data?.first().japanese?.first()?.reading ?: ""
            val parts = context.split(Regex(word))
            assert(parts.size == 2)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.padding(vertical=8.dp))
                BackButton(navController = navController)
                Spacer(modifier = Modifier.padding(vertical=3.dp))
                WordSection(
                    reading = reading,
                    word = word,
                    kanjiDict = kanjiDict,
                    navController,
                    setCurrentKanji,
                    setCurrentStory,
                    fromStudy = true
                )
                ContextSection(context_start = parts[0], word = word, context_end = parts[1])
                SmallButton(
                    backgroundColor = MaterialTheme.colors.surface,
                    text = "Add to review",
                    icon = Icons.Rounded.Add,
                    onCLick = {},
                    height = 50.dp,
                )
                Spacer(modifier = Modifier.padding(vertical=5.dp))
                Text(
                    "Similar sentences",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = TextStyle(
                        fontFamily = Quicksand,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W500
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(count = sentences.size) { index ->
                        SentenceCard(
                            sentence = sentences[index],
                            category = "Music"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SentenceCard(
    sentence: String,
    category: String,
    targetWord: String? = null
) {
    // Examples may or may not contain the target word
    // This is configurable via an option
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 5.dp,
    ) {
        Column() {
            // Category/Title row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    category,
                    style = TextStyle(
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    )
                )
            }
            // Content row
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                if(targetWord == null) {
                    SelectionContainer{
                        Text(
                            sentence,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                            style = TextStyle(
                                fontFamily = Quicksand,
                                fontWeight = FontWeight.W500,
                                fontSize = 16.sp
                            )
                        )
                    }
                } else{
                    val parts = sentence.split(Regex(targetWord))
                    val contextStart = parts[0]
                    val contextEnd = parts[1]
                    CustomAnnotatedString(
                        context_start = contextStart,
                        context_end = contextEnd,
                        word = targetWord,
                        textStyle = normalStyle,
                        specialStyle = specialStyle
                    )
                }
            }
            // BUTTON ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SmallerButton(
                    text = "Save",
                    backgroundColor = LightGreen,
                    onCLick = { /*TODO*/ },
                    height = 40.dp,
                    icon = Icons.Rounded.Favorite
                )
                SmallerButton(
                    text = "Review",
                    backgroundColor = LightBlue,
                    onCLick = { /*TODO*/ },
                    height = 40.dp,
                    icon = Icons.Rounded.Add,
                )
            }
        }
    }
}

@Composable
fun ContextSection(
    context_start: String,
    word: String,
    context_end: String,
) {
    Column (
        modifier = Modifier
            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
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
                .padding(horizontal = 8.dp)
        )

        CustomAnnotatedString(
            context_start = context_start,
            context_end = context_end,
            word = word,
            textStyle = normalStyle,
            specialStyle = specialStyle
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
fun CustomAnnotatedString(
    context_start: String,
    context_end: String,
    word: String,
    textStyle: SpanStyle,
    specialStyle: SpanStyle,
) {
    SelectionContainer{
        Text(buildAnnotatedString {
            withStyle(
                style = textStyle
            ) {
                append(context_start)
            }
            withStyle(style = specialStyle) {
                append(word)
            }
            withStyle(
                style = textStyle
            ) {
                append(context_end)
            }
        })
    }
}

@Composable
fun CustomTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
    ){
        Text(
            text,
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