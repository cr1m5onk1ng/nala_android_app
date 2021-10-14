package com.example.nala.ui.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun CustomExpandableText(
    modifier: Modifier,
    text: String,
    maxLines: Int = 5,
    style: TextStyle? = null,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onClickText: (() -> Unit)?,
) {
    val isExpanded = remember { mutableStateOf(false) }
    modifier.clickable {
        isExpanded.value = !isExpanded.value
        onClickText?.let{
            Log.d("COMMENTSDEBUG", "Function called!")
            it()
        }
    }
    Text(
        text = text,
        style = style ?: MaterialTheme.typography.body1,
        maxLines = if (isExpanded.value) Int.MAX_VALUE else maxLines,
        overflow = overflow,
    )

}