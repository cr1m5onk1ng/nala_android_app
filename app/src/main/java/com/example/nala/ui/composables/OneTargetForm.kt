package com.example.nala.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.ui.theme.*

@Composable
fun OneTargetForm(
    sentence: String,
    selectedWord: String,
    sentenceReceived: Boolean,
    tokens: List<String>,
    onSentenceAdd: (String) -> Unit,
    onWordAdd: (String) -> Unit,
    onWordSelect: (String) -> Unit,
    addSentenceToReview: (String, String) -> Unit,
    showSnackbar: () -> Unit,
    navController: NavController,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        if(!sentenceReceived) {
            LoadingIndicator()
        } else{
            //Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.End
            ){
                IconButton(
                    onClick = {
                        navController.navigate(R.id.sentence_form_to_home)
                    }
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "close icon")
                }
            }
            //Body
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item{
                    Column() {
                        CustomSelectionContainer(
                            sentence = sentence,
                        )
                        // ADD A WORD SECTIONs
                        Text(
                            text = "Select your target word: ",
                            modifier = Modifier.padding(5.dp),
                            style = TextStyle(
                                fontFamily = Quicksand,
                                fontWeight = FontWeight.W400,
                                fontSize = 20.sp
                            ),
                        )
                        TokenSelectionRow(
                            tokens = tokens,
                            onWordSelect = onWordSelect,
                            selectedToken = selectedWord,
                        )
                        Spacer(modifier = Modifier.padding(vertical=8.dp))
                        // SELECTED WORD
                        Text(
                            if(selectedWord.isNotEmpty()) selectedWord else "No word selected",
                            modifier = Modifier.fillMaxWidth(),
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
                                        onSentenceAdd(sentence)
                                        onWordAdd(selectedWord)
                                        navController.navigate(R.id.from_sentence_form_to_study)
                                    }
                                },
                                height = 50.dp,
                            )
                            SmallerButton(
                                backgroundColor = LightBlue,
                                text = "Review",
                                icon = Icons.Rounded.Add,
                                onCLick = {
                                    addSentenceToReview(selectedWord, sentence)
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

@Composable
fun TokenSelectionRow(
    tokens: List<String>,
    selectedToken: String,
    onWordSelect: (String) -> Unit,
){
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
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
    onWordSelect: (String) -> Unit,
) {
    val tokensChunks = tokens.chunked(5)
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        for(chunk in tokensChunks){
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ){
                items(count=chunk.size) {
                    for(token in chunk) {
                        TagButton(
                            onClick = {
                                onWordSelect(token)
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
    SelectionContainer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            sentence,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 24.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
            )
        )
    }
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