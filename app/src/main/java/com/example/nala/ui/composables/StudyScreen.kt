package com.example.nala.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
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
    similarSentences: List<String>,
    kanjiDict: KanjiCollection,
    navController: NavController,
    contextLoading: Boolean,
    wordLoading: Boolean,
    sentencesLoading: Boolean,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
    addSentenceToReview: (String, String) -> Unit,
    loadSimilarSentences: () -> Unit,
    scaffoldState: ScaffoldState,
    showReviewSnackbar: () -> Unit,
) {

    Scaffold(

    ) {
        if(contextLoading || wordLoading){
            LoadingIndicator()
        }
        else if(wordModel.word.isEmpty()) {
            ErrorScreen(
                text = "Target word not found in Jisho dictionary",
                subtitle = "¯\\_(ツ)_/¯"
            )
        }
        else {
            val word = wordModel.word
            val reading = wordModel.reading
            val parts = context.split(Regex(word))
            assert(parts.size == 2)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Start
                ){
                    BackButton(navController = navController)
                }
                ConstraintLayout() {
                    LazyColumn(
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
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
                            ContextSection( word = word, parts = parts)
                            SmallButton(
                                backgroundColor = MaterialTheme.colors.surface,
                                text = "Add to review",
                                icon = Icons.Rounded.Add,
                                onCLick = {
                                    addSentenceToReview(word, context)
                                    showReviewSnackbar()
                                },
                                height = 50.dp,
                            )
                            Spacer(modifier = Modifier.padding(vertical=5.dp))

                            if(similarSentences.isEmpty()) {
                                if(sentencesLoading){
                                    LoadingIndicator()
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        CustomTextButton(
                                            text = "Load similar sentences",
                                            onClick = {
                                                loadSimilarSentences()
                                            }
                                        )
                                    }
                                }
                            } else {
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
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    for(sent in similarSentences) {
                                        SentenceCard(
                                            sentence = sent,
                                            category = "Music",
                                            addSentenceToReview = addSentenceToReview,
                                            onShowReviewSnackbar = showReviewSnackbar
                                        )
                                    }
                                }
                            }
                        }
                    }
                    val snackbar = createRef()
                    DefaultSnackbar(
                        modifier = Modifier
                            .constrainAs(snackbar){
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        snackbarHostState = scaffoldState.snackbarHostState,
                        onDismiss = {
                            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                        }
                    )
                }

            }
        }
    }
}

@Composable
fun SentenceCard(
    sentence: String,
    category: String,
    targetWord: String? = null,
    addSentenceToReview: (String, String) -> Unit,
    onShowReviewSnackbar: () -> Unit,

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
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
                        parts = parts,
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
                horizontalArrangement = Arrangement.End
            ) {
                SmallerButton(
                    text = "Save",
                    backgroundColor = LightGreen,
                    onCLick = { /*TODO*/ },
                    height = 35.dp,
                    icon = Icons.Rounded.Star,
                )
                SmallerButton(
                    text = "Review",
                    backgroundColor = LightBlue,
                    onCLick = {
                        addSentenceToReview(targetWord ?: "", sentence)
                        onShowReviewSnackbar()
                              },
                    height = 35.dp,
                    icon = Icons.Rounded.Add,
                )
            }
        }
    }
}

@Composable
fun ContextSection(
    parts: List<String>,
    word: String,
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
            parts = parts,
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

