package com.mkpro.keyboard.core.keyboard

enum class SwitchType { BLUE, RED, BROWN, BLACK, SILENT }

enum class KeyAction { HID_KEY, TEXT_INSERT, LANGUAGE_SWITCH, PC_KEY_EVENT, MACRO, LAYER_SWITCH, SYSTEM_COMMAND }

/**
 * A single key on the virtual keyboard. `widthWeight` lets keys like Space
 * or Enter span more horizontal space in the row-based layout (see
 * KeyboardLayout). `hidUsageCode` is the USB HID usage ID sent over the
 * active transport; `macroId`/`command` are used when action != HID_KEY.
 */
data class KeyModel(
    val id: String,
    val label: String,
    val hidUsageCode: Int = 0,
    val widthWeight: Float = 1f,
    val action: KeyAction = KeyAction.HID_KEY,
    val macroId: String? = null,
    val command: String? = null,
    val icon: String? = null,
    /** Set only for modifier keys (Ctrl/Shift/Alt/Win) - see HidModifiers bitmask values. */
    val modifierBit: Int? = null
)
