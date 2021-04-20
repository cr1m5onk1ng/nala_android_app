package com.example.nala.ui.composables

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

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