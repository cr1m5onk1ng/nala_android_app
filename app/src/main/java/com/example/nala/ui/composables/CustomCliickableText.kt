package com.example.nala.ui.composables

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Blue700


fun getTokenFromIndexedMap(indexedTokens: Map<Pair<Int, Int>, String>, offset: Int) : String? {
    for(entry in indexedTokens) {
        val indexes = entry.key
        val start = indexes.first
        val end = indexes.second
        if(offset in start..end){
            return entry.value
        }
    }
    return null
}

@Composable
fun CustomClickableText(
    modifier: Modifier = Modifier,
    tokensMap: Map<Pair<Int, Int>, String>,
    tokens: List<String>,
    selectedToken: String,
    tokensColor: Color? = null,
    selectedColor: Color? = null,
    fontSize: TextUnit? = null,
    fontWeight: FontWeight? = null,
    onSelectWord: (String) -> Unit,
    onClick: (() -> Unit)? = null,
    ){
    val annotatedText = buildAnnotatedString {
        val activeColor = selectedColor ?: Blue700
        val normalColor = tokensColor ?: Color.Black
        val fw = fontWeight ?: FontWeight.W500
        for(t in tokens) {
            withStyle(
                style = SpanStyle(
                    color = if(t == selectedToken) activeColor else normalColor,
                    fontSize = fontSize ?: 14.sp,
                    fontWeight = if(t == selectedToken) FontWeight.Bold else fw
                )
            ) {
                append(t)
            }
        }
    }
    ClickableText(
        modifier = modifier,
        text = annotatedText,
        onClick = { offset ->
           // 1 - Loop through the keys
            // 2 - Find a key that contains the offset
            // 3 - Return the corresponding token
            val token = getTokenFromIndexedMap(tokensMap, offset)
            onSelectWord(token ?: "")
            onClick?.let{
                it()
            }
        }
    )
}
