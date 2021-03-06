package com.example.nala.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Blue700,
    primaryVariant = Blue900,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Blue700,
    primaryVariant = Blue400,
    secondary = Teal200,
    background = GreyBackground,
    surface = Color.White,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val CustomLightColorPalette = lightColors(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryVariant = SecondaryVariant,
    background = GreyBackground,
    surface = OnPrimary,
    onSurface = OnSurface,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}