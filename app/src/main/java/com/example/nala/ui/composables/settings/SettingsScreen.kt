package com.example.nala.ui.composables.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.domain.model.auth.UserModel
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.ui.composables.menus.CustomDrawer
import com.example.nala.ui.composables.menus.CustomTopBar

@Composable
fun SettingsScreen(
    isJapaneseSelected: Boolean,
    isEnglishSelected: Boolean,
    isFrenchSelected: Boolean,
    isSpanishSelected: Boolean,
    setLangSelected: (String, Boolean) -> Unit,
    scaffoldState: ScaffoldState,
    authState: AuthState<UserModel?>,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.settings_header),
                backgroundColor = MaterialTheme.colors.primary,
                scope = scope,
                scaffoldState = scaffoldState,
                navController = navController
            )
        },
        drawerContent = {
            CustomDrawer(
                modifier = Modifier.background(color = Color.White),
                scope = scope,
                authState = authState,
                onSignIn = onSignIn,
                onSignOut = onSignOut,
                scaffoldState = scaffoldState,
                navController = navController,
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
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                text = stringResource(R.string.settings_target_languages),
                style = MaterialTheme.typography.h5,
            )
            // Japanese
            SettingCard(
                lang = stringResource(R.string.japanese),
                isChecked = isJapaneseSelected,
                onChecked = {
                    setLangSelected("ja", it)
                }
            )
            SettingCard(
                lang = stringResource(R.string.english),
                isChecked = isEnglishSelected,
                onChecked = {
                    setLangSelected("en", it)
                },
                enabled = false,
            )
            SettingCard(
                lang = stringResource(R.string.french),
                isChecked = isFrenchSelected,
                onChecked = {
                    setLangSelected("fr", it)
                },
                enabled = false,
            )
            SettingCard(
                lang = stringResource(R.string.spanish),
                isChecked = isSpanishSelected,
                onChecked = {
                    setLangSelected("es", it)
                },
                enabled = false,
            )
        }
    }
}

@Composable
fun SettingCard(
    lang: String,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
    enabled: Boolean? = null,
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
                    .padding(start = 24.dp)
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.Start,

            ) {
                Text(
                    modifier = Modifier.padding(3.dp),
                    text = lang,
                    style = MaterialTheme.typography.subtitle1,
                )
                Text(
                    text = "Add ${lang.lowercase()} to your targets",
                    style = MaterialTheme.typography.body1,
                )
            }
            //CheckBox
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth(0.2f),
                verticalArrangement = Arrangement.Center,
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        onChecked(it)
                    },
                    enabled = enabled ?: true,
                )
            }
        }
    }
}