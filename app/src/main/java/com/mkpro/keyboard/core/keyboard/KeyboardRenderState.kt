package com.mkpro.keyboard.core.keyboard

/**
 * Immutable snapshot of everything needed to render a keyboard frame.
 * Used by both the in-app preview and the real IME so the visual result
 * is identical.
 */
data class KeyboardRenderState(
    val visibleRows: List<List<KeyModel>> = emptyList(),
    val activeLayerName: String = "Default",
    val isAdvancedPanelExpanded: Boolean = false
)