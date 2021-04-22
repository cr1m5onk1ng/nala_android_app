package com.example.nala.ui.composables

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun CustomAnnotatedString(
    parts: List<String>,
    word: String,
    textStyle: SpanStyle,
    specialStyle: SpanStyle,
) {
    SelectionContainer{
        Text(buildAnnotatedString {
            parts.forEachIndexed{ index, part ->
                withStyle(
                    style = textStyle
                ) {
                    append(part)
                }
                if(index < parts.size - 1) {
                    withStyle(style = specialStyle) {
                        append(word)
                    }
                }
            }
        })
    }
}