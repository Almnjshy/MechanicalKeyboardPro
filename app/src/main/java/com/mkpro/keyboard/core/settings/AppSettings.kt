package com.mkpro.keyboard.core.settings

enum class ThemeVariant { DARK, LIGHT, CYBERPUNK, NEON, MINIMAL, CLASSIC, GLASS, CARBON_FIBER }

data class AppSettings(
    val language: String = "ar",
    val keyboardOpacity: Float = 1f,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val theme: ThemeVariant = ThemeVariant.DARK
)
