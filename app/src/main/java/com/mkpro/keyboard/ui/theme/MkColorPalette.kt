package com.mkpro.keyboard.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.mkpro.keyboard.core.settings.ThemeVariant

/** All the colors any screen/component in this app needs - one instance per ThemeVariant. */
data class MkColorPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val keycap: Color,
    val keycapPressed: Color,
    val border: Color,
    val accentPurple: Color,
    val accentCyan: Color,
    val accentMagenta: Color,
    val accentGreen: Color,
    val accentAmber: Color,
    val accentRed: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

val DarkPalette = MkColorPalette(
    background = Color(0xFF0A0A0E), surface = Color(0xFF15151C), surfaceElevated = Color(0xFF1E1E28),
    keycap = Color(0xFF23232F), keycapPressed = Color(0xFF2E2E3D), border = Color(0xFF34343F),
    accentPurple = Color(0xFF9B5CFF), accentCyan = Color(0xFF00E5FF), accentMagenta = Color(0xFFFF2DA6),
    accentGreen = Color(0xFF35FFA0), accentAmber = Color(0xFFFFB020), accentRed = Color(0xFFFF4D5E),
    textPrimary = Color(0xFFF2F2F7), textSecondary = Color(0xFF9797A6)
)

val LightPalette = MkColorPalette(
    background = Color(0xFFF4F4F7), surface = Color(0xFFFFFFFF), surfaceElevated = Color(0xFFE9E9F0),
    keycap = Color(0xFFFFFFFF), keycapPressed = Color(0xFFD8D8E4), border = Color(0xFFCFCFDA),
    accentPurple = Color(0xFF7C4DFF), accentCyan = Color(0xFF00ACC1), accentMagenta = Color(0xFFE91E8C),
    accentGreen = Color(0xFF00A86B), accentAmber = Color(0xFFE0952A), accentRed = Color(0xFFE03A4C),
    textPrimary = Color(0xFF17171C), textSecondary = Color(0xFF5B5B66)
)

val CyberpunkPalette = MkColorPalette(
    background = Color(0xFF0D0014), surface = Color(0xFF1A0326), surfaceElevated = Color(0xFF250838),
    keycap = Color(0xFF1F0730), keycapPressed = Color(0xFF33104D), border = Color(0xFFFF2DA6),
    accentPurple = Color(0xFFB300FF), accentCyan = Color(0xFF00FFF0), accentMagenta = Color(0xFFFF0080),
    accentGreen = Color(0xFF39FF14), accentAmber = Color(0xFFFCEE0A), accentRed = Color(0xFFFF003C),
    textPrimary = Color(0xFFF5E9FF), textSecondary = Color(0xFFC79EDC)
)

val NeonPalette = MkColorPalette(
    background = Color(0xFF050507), surface = Color(0xFF0E0E12), surfaceElevated = Color(0xFF17171D),
    keycap = Color(0xFF121216), keycapPressed = Color(0xFF1E1E26), border = Color(0xFF00FF9C),
    accentPurple = Color(0xFF00FF9C), accentCyan = Color(0xFF00E5FF), accentMagenta = Color(0xFFFF00E5),
    accentGreen = Color(0xFF00FF9C), accentAmber = Color(0xFFFFEA00), accentRed = Color(0xFFFF1744),
    textPrimary = Color(0xFFEAFFF6), textSecondary = Color(0xFF7DFFCB)
)

val MinimalPalette = MkColorPalette(
    background = Color(0xFF1C1C1E), surface = Color(0xFF232325), surfaceElevated = Color(0xFF2C2C2E),
    keycap = Color(0xFF2A2A2C), keycapPressed = Color(0xFF3A3A3D), border = Color(0xFF3A3A3D),
    accentPurple = Color(0xFF8E8E93), accentCyan = Color(0xFF8E8E93), accentMagenta = Color(0xFF8E8E93),
    accentGreen = Color(0xFF8E8E93), accentAmber = Color(0xFF8E8E93), accentRed = Color(0xFFFF6961),
    textPrimary = Color(0xFFF2F2F7), textSecondary = Color(0xFF9797A6)
)

val ClassicPalette = MkColorPalette(
    background = Color(0xFF2B2B2B), surface = Color(0xFF383838), surfaceElevated = Color(0xFF454545),
    keycap = Color(0xFFF5F5F0), keycapPressed = Color(0xFFD8D8D0), border = Color(0xFF1A1A1A),
    accentPurple = Color(0xFF5C7AEA), accentCyan = Color(0xFF4AB8C4), accentMagenta = Color(0xFFC45A9E),
    accentGreen = Color(0xFF6FAE5C), accentAmber = Color(0xFFD1A23A), accentRed = Color(0xFFC4453A),
    textPrimary = Color(0xFF1A1A1A), textSecondary = Color(0xFFF0F0EA)
)

val GlassPalette = MkColorPalette(
    background = Color(0xFF11151C), surface = Color(0xFF1C2230), surfaceElevated = Color(0xFF262E40),
    keycap = Color(0xFF232A38), keycapPressed = Color(0xFF303A4E), border = Color(0xFF4A5A78),
    accentPurple = Color(0xFF7FB2FF), accentCyan = Color(0xFF7FF0FF), accentMagenta = Color(0xFFCB9CFF),
    accentGreen = Color(0xFF7FFFC9), accentAmber = Color(0xFFFFD98A), accentRed = Color(0xFFFF9C9C),
    textPrimary = Color(0xFFEAF2FF), textSecondary = Color(0xFFA8BAD9)
)

val CarbonFiberPalette = MkColorPalette(
    background = Color(0xFF0C0C0C), surface = Color(0xFF161616), surfaceElevated = Color(0xFF202020),
    keycap = Color(0xFF1A1A1A), keycapPressed = Color(0xFF272727), border = Color(0xFF3D3D3D),
    accentPurple = Color(0xFFB0B0B0), accentCyan = Color(0xFFD0D0D0), accentMagenta = Color(0xFFE53935),
    accentGreen = Color(0xFF9E9E9E), accentAmber = Color(0xFFE53935), accentRed = Color(0xFFE53935),
    textPrimary = Color(0xFFEDEDED), textSecondary = Color(0xFF9C9C9C)
)

fun paletteFor(variant: ThemeVariant): MkColorPalette = when (variant) {
    ThemeVariant.DARK -> DarkPalette
    ThemeVariant.LIGHT -> LightPalette
    ThemeVariant.CYBERPUNK -> CyberpunkPalette
    ThemeVariant.NEON -> NeonPalette
    ThemeVariant.MINIMAL -> MinimalPalette
    ThemeVariant.CLASSIC -> ClassicPalette
    ThemeVariant.GLASS -> GlassPalette
    ThemeVariant.CARBON_FIBER -> CarbonFiberPalette
}

val LocalMkColors = staticCompositionLocalOf { DarkPalette }
