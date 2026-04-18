package com.example.clock.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    secondary = AccentRed,
    background = DeepBackground,
    surface = SurfaceBackground,
    onPrimary = DeepBackground,
    onSecondary = DeepBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun ClockTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}