package com.example.nala.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nala.ui.theme.Blue700

@Composable
fun ShareButton(
    onShare: (String?) -> Unit,
    text: String?,
    buttonSize: Dp? = null,
    buttonColor: Color? = null,
) {
    IconButton(
        modifier = Modifier
            .padding(horizontal = 3.dp),
        onClick = {
            onShare(text)
        }
    ) {
        Icon(
            modifier = Modifier.size(buttonSize ?: 24.dp),
            imageVector = Icons.Outlined.Share,
            contentDescription = "share",
            tint = buttonColor ?: Blue700,
        )
    }
}