package com.mkpro.keyboard.ui.screens.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkpro.keyboard.core.connection.ConnectionManager
import com.mkpro.keyboard.core.connection.hid.HidReportBuilder
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.layers.LayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KeyboardUiState(
    val rows: List<List<KeyModel>> = emptyList(),
    val activeLayerId: String = "default",
    val activeLayerName: String = "Default",
    val isConnected: Boolean = false,
    val isAdvancedPanelExpanded: Boolean = false,
    /** ids of modifier keys currently held as sticky toggles (Shift/Ctrl/Alt/Win). */
    val heldModifierKeyIds: Set<String> = emptySet()
)

/**
 * Thin presentation layer over LayerManager/ConnectionManager: no business
 * logic lives here beyond mapping their StateFlows into KeyboardUiState and
 * turning key taps into HID reports.
 *
 * NOTE: kept as a true no-arg constructor (Compose's default `viewModel()`
 * resolves ViewModels via a no-arg factory). Once Hilt/a DI graph is wired
 * in, replace this with constructor injection + a ViewModelProvider.Factory.
 */
class KeyboardViewModel : ViewModel() {

    private val layerManager = LayerManager()
    private var connectionManager: ConnectionManager? = null

    fun attachConnectionManager(manager: ConnectionManager) {
        connectionManager = manager
        viewModelScope.launch {
            manager.connectionState.collect { state ->
                _uiState.value = _uiState.value.copy(isConnected = state.isConnected)
            }
        }
    }

    private val _uiState = MutableStateFlow(KeyboardUiState())
    val uiState: StateFlow<KeyboardUiState> = _uiState.asStateFlow()

    val layers get() = layerManager.layers.value

    init {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(layerManager.layers, layerManager.activeLayerId) { layers, activeId ->
                layers.firstOrNull { it.id == activeId }
            }.collect { active ->
                _uiState.value = _uiState.value.copy(
                    rows = active?.rows.orEmpty(),
                    activeLayerId = active?.id ?: "default",
                    activeLayerName = active?.name ?: "Default"
                )
            }
        }
    }

    fun cycleLanguage() = layerManager.cycleLanguage()
    fun switchLayer(layerId: String) = layerManager.switchTo(layerId)

    /**
     * Modifier keys (Shift/Ctrl/Alt/Win) toggle sticky state instead of
     * sending a report by themselves - a real HID modifier only means
     * anything combined with the next regular key. Regular keys send a
     * report built with any currently-held modifier bits, then clear them
     * (matches how a physical key combo like Ctrl+C behaves for a single tap).
     */
    fun onKeyPressed(key: KeyModel) {
        if (key.modifierBit != null) {
            val current = _uiState.value.heldModifierKeyIds
            _uiState.value = _uiState.value.copy(
                heldModifierKeyIds = if (key.id in current) current - key.id else current + key.id
            )
            return
        }

        if (key.hidUsageCode == 0) return // unmapped key (e.g. FN) - no HID report to send yet

        val activeLayerKeys = _uiState.value.rows.flatten()
        val modifierMask = _uiState.value.heldModifierKeyIds
            .mapNotNull { id -> activeLayerKeys.firstOrNull { it.id == id }?.modifierBit }
            .fold(0) { mask, bit -> mask or bit }

        viewModelScope.launch {
            connectionManager?.sendKeyEvent(HidReportBuilder.build(modifierMask, listOf(key.hidUsageCode)))
            connectionManager?.sendKeyEvent(HidReportBuilder.buildKeyUp())
        }

        // one-shot modifiers: release them after the combo is sent
        if (_uiState.value.heldModifierKeyIds.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(heldModifierKeyIds = emptySet())
        }
    }

    fun toggleAdvancedPanel() {
        _uiState.value = _uiState.value.copy(isAdvancedPanelExpanded = !_uiState.value.isAdvancedPanelExpanded)
    }
}
