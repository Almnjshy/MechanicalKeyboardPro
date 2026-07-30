package com.mkpro.keyboard.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.rgb.RgbEffectType
import com.mkpro.keyboard.ui.theme.MkBorder
import com.mkpro.keyboard.ui.theme.MkKeycap
import com.mkpro.keyboard.ui.theme.MkKeycapPressed
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.PI

/** Font size for key labels - bumped up for a bigger, easier-to-read/tap keycap. */
private val KeyLabelStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp)

/**
 * Live RGB state shared across every key in the current keyboard render -
 * computed once per frame in KeyboardIme/KeyboardScreen (one shared
 * animation clock) and passed down, rather than each KeyCap running its
 * own independent animation loop.
 */
data class RgbVisual(
    val effect: RgbEffectType,
    val color: Color,
    val brightness: Float, // 0f..1f, user-configured in Settings
    val phase: Float       // 0f..1f, continuously animated (loops every "1 unit of time")
)

/** 0..1 triangle wave: rises 0->1 then falls 1->0 over one period - used for Breathing/Wave. */
private fun triangleWave(t: Float): Float {
    val x = t - floor(t)
    return 1f - abs(2f * x - 1f)
}

private fun hsvToColor(hueDegrees: Float, saturation: Float, value: Float): Color {
    val h = ((hueDegrees % 360f) + 360f) % 360f
    val c = value * saturation
    val x = c * (1f - abs((h / 60f) % 2f - 1f))
    val m = value - c
    val (r, g, b) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m)
}

/**
 * Computes this key's live glow color for the active RGB effect.
 * `positionIndex` is this key's flattened index across the whole layout
 * (see KeyboardIme), used so Wave/Rainbow visibly travel across the
 * keyboard instead of every key changing in lockstep.
 */
private fun rgbGlowColor(rgb: RgbVisual, positionIndex: Int, isPressed: Boolean): Color? = when (rgb.effect) {
    RgbEffectType.STATIC -> rgb.color.copy(alpha = rgb.brightness)
    RgbEffectType.BREATHING -> rgb.color.copy(alpha = rgb.brightness * (0.25f + 0.75f * ((sin(rgb.phase * 2f * PI.toFloat()) + 1f) / 2f)))
    RgbEffectType.RAINBOW -> hsvToColor(rgb.phase * 360f + positionIndex * 12f, 1f, rgb.brightness)
    RgbEffectType.WAVE -> rgb.color.copy(alpha = rgb.brightness * triangleWave(rgb.phase * 2.5f - positionIndex * 0.08f))
    RgbEffectType.REACTIVE -> if (isPressed) rgb.color.copy(alpha = rgb.brightness) else null
    RgbEffectType.RIPPLE -> if (isPressed) rgb.color.copy(alpha = rgb.brightness) else null
}

/**
 * A single mechanical keycap: fills its weighted share of the row, shows a
 * short "press" travel animation, and a border/fill driven by (in priority
 * order): `accentColor` (manual override - held Shift/Ctrl/CapsLock, always
 * wins so state stays legible), the live RGB effect, or - for action keys
 * like Shift/Backspace/Enter - a subtle permanent outline in the current
 * mode's brand color (`modeAccentColor`) even when not held/pressed, so
 * they read as distinct "action keys" the way a real keyboard's accent
 * keys do.
 *
 * `repeatable = true` (used for Backspace) makes a long press fire onPress
 * repeatedly instead of only once per tap.
 */
@Composable
fun RowScope.KeyCap(
    key: KeyModel,
    accentColor: Color? = null,
    modeAccentColor: Color = MkBorder,
    isSpecialKey: Boolean = false,
    repeatable: Boolean = false,
    rgb: RgbVisual? = null,
    positionIndex: Int = 0,
    onPress: (KeyModel) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val travel by animateDpAsState(targetValue = if (isPressed) 3.dp else 0.dp, label = "keyTravel")

    if (repeatable) {
        // LaunchedEffect is cancelled automatically the instant isPressed
        // flips back to false (its key changes), so the repeat loop below
        // never needs its own "still held" check.
        LaunchedEffect(isPressed) {
            if (isPressed) {
                onPress(key) // fire immediately on press-down, not on release
                delay(350)   // initial hold delay before repeat kicks in
                while (true) {
                    onPress(key)
                    delay(50) // repeat rate while held
                }
            }
        }
    }

    val heldGlow = accentColor ?: rgb?.let { rgbGlowColor(it, positionIndex, isPressed) }
    val borderColor = heldGlow ?: if (isSpecialKey) modeAccentColor.copy(alpha = 0.55f) else MkBorder
    val borderWidth = if (heldGlow != null) 1.5.dp else if (isSpecialKey) 1.3.dp else 1.dp
    val backgroundColor = when {
        isPressed -> MkKeycapPressed
        heldGlow != null -> MkKeycapPressed
        isSpecialKey -> modeAccentColor.copy(alpha = 0.08f)
        else -> MkKeycap
    }

    Box(
        modifier = Modifier
            .weight(key.widthWeight)
            .fillMaxHeight()
            .padding(3.dp)
            .padding(top = travel)
            .background(color = backgroundColor, shape = RoundedCornerShape(10.dp))
            .border(width = borderWidth, color = borderColor, shape = RoundedCornerShape(10.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                if (!repeatable) onPress(key) // repeatable keys are driven by the LaunchedEffect above instead
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key.label,
            color = MkTextPrimary,
            style = KeyLabelStyle
        )
    }
}
