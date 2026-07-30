package com.mkpro.keyboard.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Every existing call site in the app (KeyCap, CommandBar, KeyboardIme,
 * SplashScreen, HomeScreen, ConnectionScreen...) references these exact
 * names unchanged. What changed is what's behind them: each is now a
 * @Composable property reading from LocalMkColors, so picking a different
 * ThemeVariant (see MkColorPalette.kt + Theme.kt) re-themes every one of
 * those screens with zero changes needed at the call sites.
 */
val MkBackground: Color @Composable get() = LocalMkColors.current.background
val MkSurface: Color @Composable get() = LocalMkColors.current.surface
val MkSurfaceElevated: Color @Composable get() = LocalMkColors.current.surfaceElevated
val MkKeycap: Color @Composable get() = LocalMkColors.current.keycap
val MkKeycapPressed: Color @Composable get() = LocalMkColors.current.keycapPressed
val MkBorder: Color @Composable get() = LocalMkColors.current.border

val MkAccentPurple: Color @Composable get() = LocalMkColors.current.accentPurple
val MkAccentCyan: Color @Composable get() = LocalMkColors.current.accentCyan
val MkAccentMagenta: Color @Composable get() = LocalMkColors.current.accentMagenta
val MkAccentGreen: Color @Composable get() = LocalMkColors.current.accentGreen
val MkAccentAmber: Color @Composable get() = LocalMkColors.current.accentAmber
val MkAccentRed: Color @Composable get() = LocalMkColors.current.accentRed

val MkTextPrimary: Color @Composable get() = LocalMkColors.current.textPrimary
val MkTextSecondary: Color @Composable get() = LocalMkColors.current.textSecondary
