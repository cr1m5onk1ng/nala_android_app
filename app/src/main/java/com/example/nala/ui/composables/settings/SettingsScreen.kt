package com.example.nala.ui.composables.settings

import android.widget.CheckBox
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nala.ui.composables.menus.CustomTopBar

@Composable
fun SettingsScreen(
    isJapaneseSelected: Boolean,
    isEnglishSelected: Boolean,
    isFrenchSelected: Boolean,
    isSpanishSelected: Boolean,
    setLangSelected: (String, Boolean) -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Settings",
                scope = scope,
                scaffoldState = scaffoldState,
                navController = navController
            )
        },
        backgroundColor = Color.White,
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(24.dp))
            // Japanese
            SettingCard(
                lang = "Japanese",
                isChecked = isJapaneseSelected,
                onChecked = {
                    setLangSelected("ja", it)
                }
            )
            SettingCard(
                lang = "English",
                isChecked = isEnglishSelected,
                onChecked = {
                    setLangSelected("en", it)
                }
            )
            SettingCard(
                lang = "French",
                isChecked = isFrenchSelected,
                onChecked = {
                    setLangSelected("fr", it)
                }
            )
            SettingCard(
                lang = "Spanish",
                isChecked = isSpanishSelected,
                onChecked = {
                    setLangSelected("es", it)
                }
            )
        }
    }
}

@Composable
fun SettingCard(
    lang: String,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        border = BorderStroke(Hairline, Color.LightGray),
        backgroundColor = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Title and Subtitle
            Column(
                modifier = Modifier
                    .padding(start=24.dp),
                horizontalAlignment = Alignment.Start,

            ) {
                Text(
                    modifier = Modifier.padding(3.dp),
                    text = lang,
                    style = MaterialTheme.typography.subtitle1,
                )
                Text(
                    text = "Add ${lang.lowercase()} to your target languages",
                    style = MaterialTheme.typography.body1,
                )
            }
            //CheckBox
            Column(
                modifier = Modifier.padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        onChecked(it)
                    }
                )
            }
        }
    }
}