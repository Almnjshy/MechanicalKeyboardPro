package com.mkpro.keyboard.core.keyboard

import com.mkpro.keyboard.core.connection.hid.HidModifiers

/**
 * Row-based definition of the default QWERTY layer, matching the mockup's
 * "Standard Mode". Every key carries a real USB HID usage code (Usage Page
 * 0x07) so BluetoothHidTransport can send a valid boot-protocol report -
 * this is data, not UI, so Function/Gaming/custom layers reuse the same
 * KeyCap/KeyboardScreen renderer with their own row lists.
 */
object StandardLayout {

    private val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    /** A-Z -> 0x04..0x1D */
    private fun letterKey(letter: Char) = KeyModel(
        id = letter.lowercase(), label = letter.toString(),
        hidUsageCode = 0x04 + (letters.indexOf(letter))
    )

    /** Top-row digits 1-9,0 -> 0x1E..0x27 */
    private fun digitKey(digit: Char) = KeyModel(
        id = "n$digit", label = digit.toString(),
        hidUsageCode = if (digit == '0') 0x27 else 0x1E + (digit - '1')
    )

    private fun fKey(n: Int) = KeyModel(id = "f$n", label = "F$n", hidUsageCode = 0x3A + (n - 1))

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel("esc", "ESC", hidUsageCode = 0x29),
            *(1..12).map { fKey(it) }.toTypedArray()
        ),
        listOf(
            KeyModel("grave", "`", hidUsageCode = 0x35),
            *"1234567890".map { digitKey(it) }.toTypedArray(),
            KeyModel("minus", "-", hidUsageCode = 0x2D),
            KeyModel("equal", "=", hidUsageCode = 0x2E),
            KeyModel("backspace", "⌫", hidUsageCode = 0x2A, widthWeight = 2f)
        ),
        listOf(
            KeyModel("tab", "TAB", hidUsageCode = 0x2B, widthWeight = 1.5f),
            *"QWERTYUIOP".map { letterKey(it) }.toTypedArray(),
            KeyModel("bracket_l", "[", hidUsageCode = 0x2F),
            KeyModel("bracket_r", "]", hidUsageCode = 0x30)
        ),
        listOf(
            KeyModel("caps", "CAPS", hidUsageCode = 0x39, widthWeight = 1.75f),
            *"ASDFGHJKL".map { letterKey(it) }.toTypedArray(),
            KeyModel("semicolon", ";", hidUsageCode = 0x33),
            KeyModel("quote", "'", hidUsageCode = 0x34),
            KeyModel("enter", "ENTER", hidUsageCode = 0x28, widthWeight = 2f)
        ),
        listOf(
            KeyModel("shift_l", "SHIFT", modifierBit = HidModifiers.LEFT_SHIFT, widthWeight = 2.25f),
            *"ZXCVBNM".map { letterKey(it) }.toTypedArray(),
            KeyModel("comma", ",", hidUsageCode = 0x36),
            KeyModel("period", ".", hidUsageCode = 0x37),
            KeyModel("slash", "/", hidUsageCode = 0x38),
            KeyModel("shift_r", "SHIFT", modifierBit = HidModifiers.RIGHT_SHIFT, widthWeight = 2.25f)
        ),
        listOf(
            KeyModel("ctrl_l", "CTRL", modifierBit = HidModifiers.LEFT_CTRL, widthWeight = 1.25f),
            KeyModel("win", "WIN", modifierBit = HidModifiers.LEFT_GUI, widthWeight = 1.25f),
            KeyModel("alt_l", "ALT", modifierBit = HidModifiers.LEFT_ALT, widthWeight = 1.25f),
            KeyModel("space", "SPACE", hidUsageCode = 0x2C, widthWeight = 5f),
            KeyModel("lang_switch", "🌐", action = KeyAction.LANGUAGE_SWITCH, widthWeight = 1f),
            KeyModel("alt_r", "ALT", modifierBit = HidModifiers.RIGHT_ALT, widthWeight = 1.25f),
            KeyModel("fn", "FN", widthWeight = 1.25f),
            KeyModel("ctrl_r", "CTRL", modifierBit = HidModifiers.RIGHT_CTRL, widthWeight = 1.25f)
        )
    )
}
