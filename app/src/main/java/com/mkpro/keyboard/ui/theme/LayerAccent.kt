package com.mkpro.keyboard.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Each mode/layer has its own fixed brand accent (cyan for typing, purple
 * for PC keys, orange for gaming, blue for programming...) - this is a
 * distinct concept from the user's chosen app Theme or RGB key-glow color:
 * it's "what color is this mode's chrome" (tab highlight, special-key
 * border, bottom pill), and stays consistent regardless of theme/RGB
 * settings, matching how the reference design uses one signature color
 * per mode.
 */
fun accentColorFor(layerId: String): Color = when (layerId) {
    "pc_keys" -> Color(0xFF9B6BFF)
    "gaming" -> Color(0xFFFF6A3D)
    "programming" -> Color(0xFF3D9BFF)
    "macros" -> Color(0xFF2FD9A8)
    else -> Color(0xFF00E5CC) // default (English) + Arabic typing layers
}
