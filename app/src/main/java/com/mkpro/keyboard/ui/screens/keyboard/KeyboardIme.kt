package com.mkpro.keyboard.ui.screens.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.layers.Layer
import com.mkpro.keyboard.ui.components.CommandBar
import com.mkpro.keyboard.ui.components.KeyCap
import com.mkpro.keyboard.ui.theme.MkAccentCyan
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary

/**
 * The actual keyboard panel: CommandBar on top, a horizontal strip of layer
 * tabs (English/العربية/PC Keys/...) when expanded, and the key rows below.
 * Fixed height (not fillMaxSize) - this view is hosted inside whatever app
 * the user is typing into, not the whole screen.
 */
@Composable
fun KeyboardIme(
    rows: List<List<KeyModel>>,
    activeLayerName: String,
    heldModifierKeyIds: Set<String>,
    capsLockOn: Boolean,
    isConnected: Boolean,
    isAdvancedPanelExpanded: Boolean,
    onKeyPressed: (KeyModel) -> Unit,
    onToggleAdvancedPanel: () -> Unit,
    onSwitchLayer: (String) -> Unit,
    availableLayers: List<Layer>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MkBackground)
            .padding(6.dp)
    ) {
        CommandBar(
            isConnected = isConnected,
            currentLayerName = activeLayerName,
            isExpanded = isAdvancedPanelExpanded,
            onToggleExpanded = onToggleAdvancedPanel,
            onOpenSettings = { /* TODO: open companion app settings */ },
            onOpenRgb = { /* TODO: quick RGB panel */ },
            onOpenProfile = { /* TODO: profile switcher */ }
        )

        if (isAdvancedPanelExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableLayers.forEach { layer ->
                    val isActive = layer.name == activeLayerName
                    Text(
                        text = layer.name,
                        color = if (isActive) MkTextPrimary else MkTextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                if (isActive) MkAccentCyan.copy(alpha = 0.2f) else MkSurface,
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
                .height(220.dp)
                .padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            rows.forEach { rowKeys ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    rowKeys.forEach { key ->
                        val isHeld = key.id in heldModifierKeyIds || (key.id == "caps" && capsLockOn)
                        KeyCap(
                            key = key,
                            accentColor = if (isHeld) MkAccentCyan else null,
                            onPress = onKeyPressed
                        )
                    }
                }
            }
        }
    }
}