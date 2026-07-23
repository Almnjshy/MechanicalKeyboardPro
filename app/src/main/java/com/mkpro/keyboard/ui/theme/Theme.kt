package com.mkpro.keyboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MkDarkColorScheme = darkColorScheme(
    primary = MkAccentPurple,
    secondary = MkAccentCyan,
    tertiary = MkAccentMagenta,
    background = MkBackground,
    surface = MkSurface,
    surfaceVariant = MkSurfaceElevated,
    error = MkAccentRed,
    onPrimary = MkTextPrimary,
    onBackground = MkTextPrimary,
    onSurface = MkTextPrimary,
    outline = MkBorder
)

/**
 * App-wide theme. Dark-first by design (see project spec: "Dark theme first").
 * A ThemeEngine (skinnable: Cyberpunk/Neon/Glass/Carbon Fiber/...) can later
 * swap the color scheme here without touching any screen.
 */
@Composable
fun MechanicalKeyboardProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MkDarkColorScheme,
        typography = MkTypography,
        content = content
    )
}
