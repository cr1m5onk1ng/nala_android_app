package com.example.nala.ui.composables

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nala.ui.theme.Blue500
import com.example.nala.ui.theme.Blue700


fun getTokenFromIndexedMap(indexedTokens: Map<Pair<Int, Int>, String>, offset: Int) : String? {
    Log.d("TOKENIZATIONDEBUG", "Tokens Map: $indexedTokens ")
    for(entry in indexedTokens) {
        val indexes = entry.key
        val start = indexes.first
        val end = indexes.second
        if(offset in start..end){
            Log.d("TOKENIZATIONDEBUG", "Triggered indexs: $indexes ")
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
    selectedColor: Color? = null,
    fontSize: TextUnit? = null,
    fontWeight: FontWeight? = null,
    onClick: (String) -> Unit,
    ){
    val annotatedText = buildAnnotatedString {
        val color = selectedColor ?: Blue700
        val fontWeight = fontWeight ?: FontWeight.W500
        for(t in tokens) {
            withStyle(
                style = SpanStyle(
                    color = if(t == selectedToken) color else Color.Black,
                    fontSize = fontSize ?: 14.sp,
                    fontWeight = if(t == selectedToken) FontWeight.Bold else fontWeight
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
            Log.d("TOKENIZATIONDEBUG", "offset: $offset")
            val token = getTokenFromIndexedMap(tokensMap, offset)
            onClick(token ?: "")
        }
    )
}


@Composable
fun AnnotatedClickableText() {
    val annotatedText = buildAnnotatedString {
        //append your initial text
        withStyle(
            style = SpanStyle(
                color = Color.Gray,
            )
        ) {
            append("Don't have an account? ")

        }

        //Start of the pushing annotation which you want to color and make them clickable later
        pushStringAnnotation(
            tag = "SignUp",// provide tag which will then be provided when you click the text
            annotation = "SignUp"
        )
        //add text with your different color/style
        withStyle(
            style = SpanStyle(
                color = Color.Red,
            )
        ) {
            append("Sign Up")
        }
        // when pop is called it means the end of annotation with current tag
        pop()
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = "SignUp",// tag which you used in the buildAnnotatedString
                start = offset,
                end = offset
            )[0].let { annotation ->
                //do your stuff when it gets clicked
                Log.d("Clicked", annotation.item)
            }
        }
    )
}