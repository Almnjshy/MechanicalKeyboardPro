package com.mkpro.keyboard.core.settings

import com.mkpro.keyboard.core.rgb.RgbEffectType

enum class ThemeVariant { DARK, LIGHT, CYBERPUNK, NEON, MINIMAL, CLASSIC, GLASS, CARBON_FIBER }

data class AppSettings(
    val language: String = "ar",
    val keyboardOpacity: Float = 1f,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val theme: ThemeVariant = ThemeVariant.DARK,
    val rgbEffect: RgbEffectType = RgbEffectType.STATIC,
    val rgbColorArgb: Int = 0xFF9B5CFF.toInt(), // default: purple accent
    val rgbBrightness: Float = 1f,
    val rgbSpeed: Float = 1f
)
