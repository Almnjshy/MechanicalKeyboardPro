package com.mkpro.keyboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.mkpro.keyboard.core.settings.ThemeVariant

private fun colorSchemeFor(palette: MkColorPalette, variant: ThemeVariant) =
    if (variant == ThemeVariant.LIGHT) {
        lightColorScheme(
            primary = palette.accentPurple, secondary = palette.accentCyan, tertiary = palette.accentMagenta,
            background = palette.background, surface = palette.surface, surfaceVariant = palette.surfaceElevated,
            error = palette.accentRed, onPrimary = palette.textPrimary, onBackground = palette.textPrimary,
            onSurface = palette.textPrimary, outline = palette.border
        )
    } else {
        darkColorScheme(
            primary = palette.accentPurple, secondary = palette.accentCyan, tertiary = palette.accentMagenta,
            background = palette.background, surface = palette.surface, surfaceVariant = palette.surfaceElevated,
            error = palette.accentRed, onPrimary = palette.textPrimary, onBackground = palette.textPrimary,
            onSurface = palette.textPrimary, outline = palette.border
        )
    }

/**
 * App-wide theme. `themeVariant` picks one of the 8 palettes from
 * MkColorPalette.kt (Dark/Light/Cyberpunk/Neon/Minimal/Classic/Glass/Carbon
 * Fiber, per spec). Providing it via CompositionLocalProvider means every
 * screen that reads MkBackground/MkAccentCyan/etc (see Color.kt) re-themes
 * automatically - no screen needs to know which variant is active.
 */
@Composable
fun MechanicalKeyboardProTheme(
    themeVariant: ThemeVariant = ThemeVariant.DARK,
    content: @Composable () -> Unit
) {
    val palette = paletteFor(themeVariant)
    CompositionLocalProvider(LocalMkColors provides palette) {
        MaterialTheme(
            colorScheme = colorSchemeFor(palette, themeVariant),
            typography = MkTypography,
            content = content
        )
    }
}
