package com.example.nala.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.ui.theme.*

@Composable
fun OneTargetForm(
    sentence: String,
    selectedWord: String,
    onSentenceAdd: (String) -> Unit,
    onWordAdd: (String) -> Unit,
    onWordSelect: (String) -> Unit,
    navController: NavController,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ){
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Context section
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
            Spacer(modifier = Modifier.padding(vertical=8.dp))
            // Word selection
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
                        onSentenceAdd(sentence)
                        onWordAdd(selectedWord)
                        navController.navigate(R.id.from_sentence_form_to_study)
                    },
                    height = 50.dp,
                )
                SmallerButton(
                    backgroundColor = LightBlue,
                    text = "Review",
                    icon = Icons.Rounded.Add,
                    onCLick = {},
                    height = 50.dp,
                )
            }
        }
    }
}