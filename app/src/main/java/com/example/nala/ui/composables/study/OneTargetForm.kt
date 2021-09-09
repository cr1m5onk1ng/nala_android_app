package com.example.nala.ui.composables.study

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.*
import com.example.nala.ui.theme.*

@Composable
fun OneTargetForm(
    sentenceState: DataState<String>,
    selectedWord: String,
    fromLookup: Boolean = false,
    tokens: List<String>,
    tokensIndexMap: Map<Pair<Int, Int>, String>,
    onSentenceAdd: (String) -> Unit,
    onWordAdd: (String) -> Unit,
    onWordSelect: (String) -> Unit,
    setKanjis: (String) -> Unit,
    unsetSelectedWord: () -> Unit,
    unsetSharedSentence: () -> Unit,
    addSentenceToReview: (String, String) -> Unit,
    loadSentenceReviews: () -> Unit,
    navController: NavController,
    scaffoldState: ScaffoldState,
    showSnackbar: (ScaffoldState) -> Unit
) {
    val activity = (LocalContext.current as? Activity)

    Scaffold(){ paddingValue ->
        ConstraintLayout(
            modifier = Modifier.padding(paddingValue)
        ){
            Column() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(
                        onClick = {
                            unsetSharedSentence()
                            unsetSelectedWord()
                            if (fromLookup)
                                activity!!.finish()
                            else
                                navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "close icon")
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    when(sentenceState) {
                        is DataState.Initial<*>, DataState.Loading -> {
                            LoadingIndicator()
                        }
                        is DataState.Error -> {
                            ErrorScreen(
                                text = stringResource(R.string.target_form_invalid_data),
                                stringResource(R.string.target_form_invalid_data_sub)
                            )
                        }
                        is DataState.Success<String> -> {
                            val sentence = sentenceState.data

                            //Body
                            val listState = rememberLazyListState()
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                item{
                                    Column() {
                                        Text(
                                            text = stringResource(R.string.target_form_select_word),
                                            modifier = Modifier.padding(5.dp),
                                            style = TextStyle(
                                                fontFamily = Quicksand,
                                                fontWeight = FontWeight.Light,
                                                fontSize = 20.sp
                                            ),
                                        )
                                        CustomClickableText(
                                            modifier = Modifier
                                                .padding(8.dp),
                                            tokens = tokens,
                                            tokensMap = tokensIndexMap,
                                            selectedToken = selectedWord,
                                            onSelectWord = onWordSelect,
                                            fontSize = 24.sp,
                                        )
                                        Spacer(modifier = Modifier.padding(vertical=8.dp))
                                        // SELECTED WORD
                                        Text(
                                            if(selectedWord.isNotEmpty()) selectedWord else stringResource(R.string.target_form_no_word_selected),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            textAlign = TextAlign.Center,
                                            style = TextStyle(
                                                fontFamily = Quicksand,
                                                fontSize = if(selectedWord.isNotEmpty()) 42.sp else 18.sp,
                                                fontWeight = if(selectedWord.isNotEmpty()) FontWeight.Bold else FontWeight.Light,
                                                color = Color.Black,
                                            ),
                                        )
                                        Spacer(modifier = Modifier.padding(vertical=16.dp))
                                        // Buttons row
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            SmallerButton(
                                                backgroundColor = LightGreen,
                                                text = stringResource(R.string.study_button),
                                                icon = Icons.Rounded.ArrowForward,
                                                onCLick = {
                                                    if(selectedWord.isNotEmpty()) {
                                                        onWordAdd(selectedWord)
                                                        setKanjis(selectedWord)
                                                        onSentenceAdd(sentence)
                                                        navController.navigate("study_screen")
                                                    }
                                                },
                                                height = 50.dp,
                                            )
                                            SmallerButton(
                                                backgroundColor = LightBlue,
                                                text = "Review",
                                                icon = Icons.Rounded.Add,
                                                onCLick = {
                                                    if (selectedWord.isNotEmpty()) {
                                                        addSentenceToReview(selectedWord, sentence)
                                                        showSnackbar(scaffoldState)
                                                        loadSentenceReviews()
                                                    }
                                                },
                                                height = 50.dp,
                                            )
                                        }
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

