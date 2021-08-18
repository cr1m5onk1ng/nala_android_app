package com.example.nala.ui.composables.study

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
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
                            ErrorScreen(text = "Data invalid", "Try to import a valid sentence")
                        }
                        is DataState.Success<String> -> {
                            val sentence = sentenceState.data

                            //Body
                            val listState = rememberLazyListState()
                            // Remember a CoroutineScope to be able to launch
                            val coroutineScope = rememberCoroutineScope()
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
                                            text = "Tap to select your target word: ",
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
                                            if(selectedWord.isNotEmpty()) selectedWord else "No word selected",
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
                                                text = "Study",
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

@Composable
fun TokenSelectionRow(
    tokens: List<String>,
    selectedToken: String,
    onWordSelect: (String) -> Unit,
){
    LazyRow(
        modifier = Modifier
            .padding(8.dp)
            .background(color = Color.White),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ){
        items(count=tokens.size){
            for (token in tokens) {
                TagButton(
                    onClick = {
                        onWordSelect(token)
                    },
                    text = token,
                    textSize = 16.sp,
                    textWeight = FontWeight.W500,
                    height = 50.dp,
                    padding = 3.dp,
                    backgroundColor =
                    if(token == selectedToken) LightBlue else Color.White
                )
            }
        }
    }
}

@Composable
fun TokensContainer(
    tokens: List<String>,
    selectedToken: String,
    chunk: Int = 5,
    onWordSelect: (String) -> Unit,
    onClick: (() -> Unit)? = null,
) {
    val tokensChunks = tokens.chunked(chunk)
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        for(chunk in tokensChunks){
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .height(55.dp),
                horizontalArrangement = Arrangement.Center
            ){
                items(count=chunk.size) {
                    for(token in chunk) {
                        TagButton(
                            onClick = {
                                onWordSelect(token)
                                onClick?.let{ onCLick ->
                                    onClick()
                                }
                            },
                            text = token,
                            textSize = 14.sp,
                            textWeight = FontWeight.W500,
                            height = 40.dp,
                            padding = 0.dp,
                            backgroundColor =
                                if(token == selectedToken) LightBlue else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSelectionContainer(
    sentence: String,
) {
    ClickableText(
        text = AnnotatedString(sentence),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = { offset ->
            Log.d("TextDebug", "offset: $offset")

        }
    )
}

@Composable
fun CustomOutlinedTextField(
    selectedWord: String,
    onWordSelect: (String) -> Unit,
) {
    OutlinedTextField(
        value = selectedWord,
        onValueChange = onWordSelect,
        label = { Text("Word") },
        placeholder = { Text("Select a target word") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Blue500,
            focusedLabelColor = Blue700,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default,
        ),
    )
}