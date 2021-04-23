package com.example.nala.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nala.ui.theme.Blue700

@Composable
fun AddToReviewButton(
    iconColor: Color? = null,
    textColor: Color? = null,
    scaffoldState: ScaffoldState,
    addToReview: () -> Unit,
    onShowSnackbar: () -> Unit,
){
    Button(
        onClick = {
            addToReview()
            onShowSnackbar()
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(18.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Blue700
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "add to review", tint=iconColor ?: Color.White)
            Spacer(modifier= Modifier.padding(horizontal = 5.dp))
            Text("Add to review", color = textColor ?: Color.White)
        }
    }
}