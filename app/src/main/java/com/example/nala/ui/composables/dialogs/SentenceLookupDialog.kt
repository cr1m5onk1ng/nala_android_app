package com.example.nala.ui.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nala.ui.composables.CustomClickableText
import com.example.nala.ui.composables.SmallerButton
import com.example.nala.ui.theme.Blue500
import com.example.nala.ui.theme.GreyBackground

@Composable
fun SentenceLookupDialog(
    sentence: String,
    tokensMap: Map<Pair<Int, Int>, String>,
    tokens: List<String>,
    selectedWord: String,
    onSetSelectedWord: (String) -> Unit,
    onSearchWord: () -> Unit,
    onSetDialogOpen: (Boolean) -> Unit,
    onSaveSentence: (String, String) -> Unit,
    onShowAddedSentenceSnackbar: () -> Unit,
 ) {
    Dialog(
        onDismissRequest = { onSetDialogOpen(false) },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ){
        Box(
            contentAlignment= Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .background(
                    GreyBackground,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("Recognized Text:", style = MaterialTheme.typography.subtitle1)
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomClickableText(
                        modifier = Modifier.padding(8.dp),
                        tokensColor = Color.Gray,
                        selectedColor = Color.Black,
                        textDecoration = TextDecoration.Underline,
                        tokensMap = tokensMap,
                        tokens = tokens,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        selectedToken = selectedWord,
                        onSelectWord = onSetSelectedWord,
                        onClick = { onSearchWord() }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    SmallerButton(
                        text = "Review",
                        backgroundColor = Blue500,
                        onCLick = {
                            onSaveSentence(selectedWord, sentence)
                            onShowAddedSentenceSnackbar()
                            onSetDialogOpen(false)
                        },
                        height = 50.dp,
                        icon = Icons.Rounded.Add,
                    )
                }
            }
        }
    }
}

