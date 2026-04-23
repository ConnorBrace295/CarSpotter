package com.example.carspotter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CarPrimary,
    secondary = CarSecondary,

    background = CarBackground,
    surface = CarSurface,
    surfaceVariant = CarSurfaceVariant,

    onPrimary = Color.Black,
    onSecondary = Color.White,

    onBackground = CarTextPrimary,
    onSurface = CarTextPrimary,
    onSurfaceVariant = CarTextSecondary
)

@Composable
fun CarSpotterTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}