package com.mkpro.keyboard.core.layers

import com.mkpro.keyboard.core.keyboard.ArabicLayout
import com.mkpro.keyboard.core.keyboard.PcKeysLayout
import com.mkpro.keyboard.core.keyboard.StandardLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the set of available layers (Default, Arabic, PC keys, Programming,
 * Gaming, Macros, custom, ...) and the currently active one. Switching is
 * just a state update - no I/O - so it's instant per spec.
 */
class LayerManager {

    private val builtInLayers = listOf(
        Layer(id = "default", name = "English", rows = StandardLayout.rows()),
        Layer(id = "arabic", name = "العربية", rows = ArabicLayout.rows()),
        Layer(id = "pc_keys", name = "PC Keys", rows = PcKeysLayout.rows())
        // Programming / Gaming / Macros layers are added the same way once
        // their key rows are defined.
    )

    private val _layers = MutableStateFlow(builtInLayers)
    val layers: StateFlow<List<Layer>> = _layers.asStateFlow()

    private val _activeLayerId = MutableStateFlow(builtInLayers.first().id)
    val activeLayerId: StateFlow<String> = _activeLayerId.asStateFlow()

    fun switchTo(layerId: String) {
        if (_layers.value.any { it.id == layerId }) {
            _activeLayerId.value = layerId
        }
    }

    /** Cycles between the installed language layers (English/Arabic today). */
    fun cycleLanguage() {
        val languageLayerIds = listOf("default", "arabic")
        val currentIndex = languageLayerIds.indexOf(_activeLayerId.value)
        val nextId = if (currentIndex == -1) languageLayerIds.first()
        else languageLayerIds[(currentIndex + 1) % languageLayerIds.size]
        switchTo(nextId)
    }

    fun addCustomLayer(layer: Layer) {
        _layers.value = _layers.value + layer
    }

    fun removeLayer(layerId: String) {
        _layers.value = _layers.value.filterNot { it.id == layerId }
    }
}
