package com.example.nala.ui.composables.study

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.*
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
    studyContextState: DataState<String>,
    targetWordState: DataState<DictionaryModel>,
    similarSentencesState: DataState<List<String>>,
    navController: NavController,
    setSharedSentence: (String) -> Unit,
    setCurrentWord: (String) -> Unit,
    unsetTargetWord: () -> Unit,
    addSentenceToReview: (String, String) -> Unit,
    loadSimilarSentences: () -> Unit,
    onShare: (String?) -> Unit,
    setIsWordFromForm: () -> Unit,
    scaffoldState: ScaffoldState,
    showReviewSnackbar: () -> Unit,
    showSaveSnackbar: () -> Unit,
) {

    Scaffold(

    ) { paddingValue ->
        when(studyContextState){
            is DataState.Initial<*>, DataState.Loading -> {
                LoadingIndicator()
            }
            is DataState.Error -> {
                ErrorScreen(
                    text = stringResource(R.string.study_data_fetch_error),
                    subtitle = "??\\_(???)_/??"
                )
            }
            is DataState.Success<String> -> {
                val context = studyContextState.data
                when(targetWordState) {
                    is DataState.Initial<*>, DataState.Loading -> {
                        LoadingIndicator()
                    }
                    is DataState.Error -> {
                        ErrorScreen(
                            text = stringResource(R.string.study_data_word_fetch_error),
                            subtitle = "??\\_(???)_/??"
                        )
                    }
                    is DataState.Success<DictionaryModel> -> {
                        val wordModel = targetWordState.data
                        ConstraintLayout(modifier = Modifier.padding(paddingValue)) {
                            val word = wordModel.word
                            val parts = context.split(Regex(word))
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
                                    BackButton(
                                        navController = navController,
                                        iconColor = Color.Black,
                                        cleanupFunction = {
                                            unsetTargetWord()
                                        }
                                    )
                                }

                                LazyColumn(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    item {
                                        Spacer(modifier = Modifier.padding(vertical=3.dp))
                                        WordRow(
                                            reading = wordModel.reading,
                                            word = wordModel.word,
                                            sentence = context,
                                            setCurrentWord = setCurrentWord,
                                            setIsWordFromForm = setIsWordFromForm,
                                            onShare = onShare,
                                            navController = navController,
                                        )
                                        ContextSection( word = word, parts = parts)
                                        AddToReviewButton(
                                            scaffoldState = scaffoldState,
                                            addToReview = { addSentenceToReview(word, context) },
                                            onShowSnackbar = { showReviewSnackbar() }
                                        )
                                        Spacer(modifier = Modifier.padding(vertical=5.dp))
                                        when(similarSentencesState) {
                                            is DataState.Initial<*> -> {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    CustomTextButton(
                                                        text = stringResource(R.string.see_similar_sentences),
                                                        onClick = {
                                                            loadSimilarSentences()
                                                        }
                                                    )
                                                }
                                            }
                                            is DataState.Loading -> {
                                                LoadingIndicator()
                                            }
                                            is DataState.Error -> {
                                                ErrorScreen(
                                                    text = stringResource(R.string.similar_sentences_fetch_error),
                                                    subtitle = "??\\_(???)_/??"
                                                )
                                            }
                                            is DataState.Success<List<String>> -> {
                                                val similarSentences = similarSentencesState.data
                                                Text(
                                                    stringResource(R.string.similar_sentences_button),
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
                                                            setSharedSentence = setSharedSentence,
                                                            unsetTargetWord = unsetTargetWord,
                                                            showSaveSnackbar = showSaveSnackbar,
                                                            navController = navController,
                                                        )
                                                    }
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordRow(
    reading: String,
    word: String,
    sentence: String,
    setCurrentWord: (String) -> Unit,
    setIsWordFromForm: () -> Unit,
    onShare: (String?) -> Unit,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 32.dp)
            .clickable {
                setCurrentWord(word)
                setIsWordFromForm()
                navController.navigate("detail_screen")
            }
    ){
        Text(
            reading,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                word,
                style = TextStyle(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            )
            ShareButton(onShare = onShare, text = sentence, buttonSize = 28.dp)
        }

    }
}

@Composable
fun SentenceCard(
    sentence: String,
    category: String,
    targetWord: String? = null,
    setSharedSentence: (String) -> Unit,
    unsetTargetWord: () -> Unit,
    showSaveSnackbar: () -> Unit,
    navController: NavController,

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
                    text = stringResource(R.string.save_button),
                    textColor = Color.Black,
                    backgroundColor = LightGreen,
                    onCLick = {
                              //TODO(ADD TO CORPUS FUNCTIONALITY)
                              showSaveSnackbar()
                    },
                    height = 35.dp,
                    icon = Icons.Rounded.Star,
                )
                SmallerButton(
                    text = "Study",
                    textColor = Color.Black,
                    backgroundColor = LightBlue,
                    onCLick = {
                        //addSentenceToReview(targetWord ?: "", sentence)
                        //showReviewSnackbar()
                        //loadSentenceReviews()
                        unsetTargetWord()
                        setSharedSentence(sentence)
                        navController.navigate("sentence_form_screen")
                              },
                    height = 35.dp,
                    icon = Icons.Rounded.ArrowForward,
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

