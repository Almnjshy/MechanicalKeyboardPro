package com.mkpro.keyboard.ui.screens.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.keyboard.KeyboardRenderState
import com.mkpro.keyboard.ui.components.CommandBar
import com.mkpro.keyboard.ui.components.KeyCap
import com.mkpro.keyboard.ui.theme.MkBackground

/**
 * Renders whatever KeyboardRenderState it's given, with no dependency on
 * where that state comes from - the real IME (KeyboardService) and the
 * in-app preview both use this same composable, so what you customize in
 * the preview is exactly what you'll type with everywhere else.
 */
@Composable
fun KeyboardScreen(
    render: KeyboardRenderState,
    isConnected: Boolean,
    displayLabelFor: (KeyModel) -> String,
    onKeyPressed: (KeyModel) -> Unit,
    onToggleExpanded: () -> Unit,
    onCycleLayer: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenRgb: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground)
            .padding(10.dp)
    ) {
        CommandBar(
            isConnected = isConnected,
            currentLayerName = render.activeLayerName,
            isExpanded = render.isAdvancedPanelExpanded,
            onToggleExpanded = onToggleExpanded,
            onOpenSettings = onOpenSettings,
            onOpenRgb = onOpenRgb,
            onOpenProfile = onOpenProfile,
            onCycleLayer = onCycleLayer
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            render.visibleRows.forEach { rowKeys ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    rowKeys.forEach { key ->
                        KeyCap(
                            key = key,
                            displayLabel = displayLabelFor(key),
                            onPress = onKeyPressed
                        )
                    }
                }
            }
        }
    }
}

/** Convenience overload for the in-app preview/onboarding screen, backed by a ViewModel. */
@Composable
fun KeyboardScreen(viewModel: KeyboardViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    KeyboardScreen(
        render = uiState.render,
        isConnected = uiState.isConnected,
        displayLabelFor = viewModel::displayLabelFor,
        onKeyPressed = viewModel::onKeyPressed,
        onToggleExpanded = viewModel::toggleAdvancedPanel,
        onCycleLayer = viewModel::cycleLayer
    )
}
