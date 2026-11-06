package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GaniColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = DeepNavyDark,
    primaryContainer = NavyElevated,
    onPrimaryContainer = GoldLight,
    secondary = GoldSecondary,
    onSecondary = DeepNavyDark,
    secondaryContainer = NavyCard,
    onSecondaryContainer = GoldLight,
    tertiary = GoldLight,
    onTertiary = DeepNavyDark,
    background = NavyBackground,
    onBackground = TextWhite,
    surface = NavySurface,
    onSurface = TextWhite,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextMuted,
    outline = NavyBorder,
    outlineVariant = Color(0x33FFD700),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GaniColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun GaniAlmalTheme(
    content: @Composable () -> Unit
) {
    MyApplicationTheme(darkTheme = true, dynamicColor = false, content = content)
}
