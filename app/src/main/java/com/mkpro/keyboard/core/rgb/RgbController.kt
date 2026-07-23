package com.mkpro.keyboard.core.rgb

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RgbEffectType { STATIC, BREATHING, WAVE, REACTIVE, RIPPLE, RAINBOW }

data class RgbSettings(
    val effect: RgbEffectType = RgbEffectType.STATIC,
    val colors: List<Color> = listOf(Color(0xFF9B5CFF)),
    val brightness: Float = 1f,       // 0f..1f
    val animationSpeed: Float = 1f,   // multiplier
    val perKeyOverrides: Map<String, Color> = emptyMap() // keyId -> color
)

/**
 * Holds the current RGB configuration. KeyboardScreen keys read from this
 * to render their glow; it does not itself run the animation loop -
 * per-effect animation timing is handled in Compose (see KeyboardScreen)
 * driven off `effect`/`animationSpeed` here.
 */
class RgbController {

    private val _settings = MutableStateFlow(RgbSettings())
    val settings: StateFlow<RgbSettings> = _settings.asStateFlow()

    fun setEffect(effect: RgbEffectType) {
        _settings.value = _settings.value.copy(effect = effect)
    }

    fun setColors(colors: List<Color>) {
        _settings.value = _settings.value.copy(colors = colors)
    }

    fun setBrightness(brightness: Float) {
        _settings.value = _settings.value.copy(brightness = brightness.coerceIn(0f, 1f))
    }

    fun setAnimationSpeed(speed: Float) {
        _settings.value = _settings.value.copy(animationSpeed = speed.coerceIn(0.1f, 4f))
    }

    fun setKeyColor(keyId: String, color: Color) {
        _settings.value = _settings.value.copy(
            perKeyOverrides = _settings.value.perKeyOverrides + (keyId to color)
        )
    }
}
