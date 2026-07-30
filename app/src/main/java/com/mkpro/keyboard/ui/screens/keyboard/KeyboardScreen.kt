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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkpro.keyboard.MkProApplication
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
 * PC-connection mode preview/simulator - full-screen version of the same
 * keyboard used inside KeyboardService, kept visually identical (same
 * header/tabs/quick-actions/bottom bar) to the IME.
 */
@Composable
fun KeyboardScreen(viewModel: KeyboardViewModel = viewModel(), onOpenSettings: () -> Unit = {}) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val connectionManager = (context.applicationContext as MkProApplication).connectionManager
        viewModel.attachConnectionManager(connectionManager)
    }

    val settingsRepository = (context.applicationContext as MkProApplication).settingsRepository
    val settings by settingsRepository.settingsFlow.collectAsState(initial = AppSettings())
    val accent = accentColorFor(uiState.activeLayerId)
    val specialKeyIds = if (uiState.activeLayerId == "gaming") SPECIAL_KEY_IDS + GAMING_HIGHLIGHT_IDS else SPECIAL_KEY_IDS

    val infiniteTransition = rememberInfiniteTransition(label = "rgbPhase")
    val durationMs = (4000 / settings.rgbSpeed.coerceIn(0.15f, 5f)).toInt().coerceAtLeast(200)
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMs, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "rgbPhaseValue"
    )
    val rgbVisual = RgbVisual(
        effect = settings.rgbEffect,
        color = Color(settings.rgbColorArgb),
        brightness = settings.rgbBrightness,
        phase = phase
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground)
            .padding(10.dp)
    ) {
        BrandHeaderBar(
            accentColor = accent,
            isExpanded = uiState.isAdvancedPanelExpanded,
            onToggleExpanded = viewModel::toggleAdvancedPanel,
            onOpenMenu = viewModel::toggleAdvancedPanel,
            onOpenSettings = onOpenSettings
        )

        Box(modifier = Modifier.padding(top = 6.dp)) {
            ModeTabRow(activeLayerId = uiState.activeLayerId, accentColor = accent, onOpenSettings = onOpenSettings)
        }

        if (uiState.activeLayerId in TYPING_LAYER_IDS) {
            Box(modifier = Modifier.padding(top = 6.dp)) {
                LanguageSwitcherRow(
                    languageCode = if (uiState.activeLayerId == "arabic") "AR" else "EN",
                    languageName = uiState.activeLayerName,
                    onCycle = viewModel::cycleLanguage
                )
            }
        } else {
            Box(modifier = Modifier.padding(top = 6.dp)) {
                QuickActionRow(layerId = uiState.activeLayerId, accentColor = accent, onOpenSettings = onOpenSettings)
            }
        }

        if (uiState.isAdvancedPanelExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                viewModel.layers.forEach { layer ->
                    val isActive = layer.id == uiState.activeLayerId
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
                            .clickable { viewModel.switchLayer(layer.id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            uiState.rows.forEachIndexed { rowIndex, rowKeys ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    rowKeys.forEachIndexed { colIndex, key ->
                        val isHeld = key.id in uiState.heldModifierKeyIds
                        KeyCap(
                            key = key,
                            accentColor = if (isHeld) accent else null,
                            modeAccentColor = accent,
                            isSpecialKey = key.id in specialKeyIds,
                            repeatable = key.id == "backspace",
                            rgb = rgbVisual,
                            positionIndex = rowIndex * 12 + colIndex,
                            onPress = viewModel::onKeyPressed
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.padding(top = 6.dp)) {
            BottomActionBar(modeName = uiState.activeLayerName, accentColor = accent, onGlobeClick = viewModel::cycleLanguage)
        }

        DragHandle(modifier = Modifier.padding(top = 4.dp))
    }
}
