package com.mkpro.keyboard.ui.screens.keyboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.layers.Layer
import com.mkpro.keyboard.core.settings.AppSettings
import com.mkpro.keyboard.ui.components.BottomActionBar
import com.mkpro.keyboard.ui.components.BrandHeaderBar
import com.mkpro.keyboard.ui.components.DragHandle
import com.mkpro.keyboard.ui.components.KeyCap
import com.mkpro.keyboard.ui.components.LanguageSwitcherRow
import com.mkpro.keyboard.ui.components.ModeTabRow
import com.mkpro.keyboard.ui.components.QuickActionRow
import com.mkpro.keyboard.ui.components.RgbVisual
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary
import com.mkpro.keyboard.ui.theme.accentColorFor

private val TYPING_LAYER_IDS = setOf("default", "arabic")
private val SPECIAL_KEY_IDS = setOf("shift_l", "shift_r", "backspace", "enter")
private val GAMING_HIGHLIGHT_IDS = setOf("w", "a", "s", "d")

/**
 * The actual keyboard panel: thin icon-only header, mode tab row, then
 * either a language switcher (typing layers) or a labeled quick-action row
 * (Gaming/Programming/PC Keys), a horizontal layer-switch strip when
 * expanded, the key rows, a bottom action bar, and a drag handle - matches
 * the reference design's per-mode look. Fixed height (not fillMaxSize) -
 * this view is hosted inside whatever app the user is typing into.
 */
@Composable
fun KeyboardIme(
    rows: List<List<KeyModel>>,
    activeLayerId: String,
    activeLayerName: String,
    heldModifierKeyIds: Set<String>,
    capsLockOn: Boolean,
    isConnected: Boolean,
    isAdvancedPanelExpanded: Boolean,
    onKeyPressed: (KeyModel) -> Unit,
    onToggleAdvancedPanel: () -> Unit,
    onSwitchLayer: (String) -> Unit,
    onCycleLanguage: () -> Unit,
    availableLayers: List<Layer>,
    appSettings: AppSettings = AppSettings(),
    onOpenSettings: () -> Unit = {}
) {
    val accent = accentColorFor(activeLayerId)
    val specialKeyIds = if (activeLayerId == "gaming") SPECIAL_KEY_IDS + GAMING_HIGHLIGHT_IDS else SPECIAL_KEY_IDS

    val infiniteTransition = rememberInfiniteTransition(label = "rgbPhase")
    val durationMs = (4000 / appSettings.rgbSpeed.coerceIn(0.15f, 5f)).toInt().coerceAtLeast(200)
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMs, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "rgbPhaseValue"
    )
    val rgbVisual = RgbVisual(
        effect = appSettings.rgbEffect,
        color = Color(appSettings.rgbColorArgb),
        brightness = appSettings.rgbBrightness,
        phase = phase
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MkBackground)
            .navigationBarsPadding()
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        BrandHeaderBar(
            accentColor = accent,
            isExpanded = isAdvancedPanelExpanded,
            onToggleExpanded = onToggleAdvancedPanel,
            onOpenMenu = onToggleAdvancedPanel,
            onOpenSettings = onOpenSettings
        )

        Box(modifier = Modifier.padding(top = 6.dp)) {
            ModeTabRow(activeLayerId = activeLayerId, accentColor = accent, onOpenSettings = onOpenSettings)
        }

        if (activeLayerId in TYPING_LAYER_IDS) {
            Box(modifier = Modifier.padding(top = 6.dp)) {
                LanguageSwitcherRow(
                    languageCode = if (activeLayerId == "arabic") "AR" else "EN",
                    languageName = activeLayerName,
                    onCycle = onCycleLanguage
                )
            }
        } else {
            Box(modifier = Modifier.padding(top = 6.dp)) {
                QuickActionRow(layerId = activeLayerId, accentColor = accent, onOpenSettings = onOpenSettings)
            }
        }

        if (isAdvancedPanelExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableLayers.forEach { layer ->
                    val isActive = layer.id == activeLayerId
                    val layerAccent = accentColorFor(layer.id)
                    Text(
                        text = layer.name,
                        color = if (isActive) MkTextPrimary else MkTextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                if (isActive) layerAccent.copy(alpha = 0.2f) else MkSurface,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSwitchLayer(layer.id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(258.dp)
                .padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            rows.forEachIndexed { rowIndex, rowKeys ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    rowKeys.forEachIndexed { colIndex, key ->
                        val isHeld = key.id in heldModifierKeyIds || (key.id == "caps" && capsLockOn)
                        KeyCap(
                            key = key,
                            accentColor = if (isHeld) accent else null,
                            modeAccentColor = accent,
                            isSpecialKey = key.id in specialKeyIds,
                            repeatable = key.id == "backspace",
                            rgb = rgbVisual,
                            positionIndex = rowIndex * 12 + colIndex,
                            onPress = onKeyPressed
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.padding(top = 6.dp)) {
            BottomActionBar(modeName = activeLayerName, accentColor = accent, onGlobeClick = onCycleLanguage)
        }

        DragHandle(modifier = Modifier.padding(top = 4.dp))
    }
}
