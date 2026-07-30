package com.mkpro.keyboard.core.keyboard

import com.mkpro.keyboard.core.connection.hid.HidModifiers

/**
 * "Gaming" layer: a dedicated G1-G6 macro row up top, then a standard
 * QWERTY typing area underneath (number row + full letters) so the same
 * layer still works for chat/game UI text entry, not just WASD. W/A/S/D
 * are tagged with distinct ids so the UI layer (KeyboardIme) can give them
 * a permanent highlight border, matching the reference design's "WASD is
 * highlighted" requirement - StandardLayout's letters intentionally use
 * the same ids (w/a/s/d) so this works without any special-casing here.
 */
object GamingLayout {

    private fun letter(id: String, label: String, hidUsageCode: Int) =
        KeyModel(id = id, label = label, hidUsageCode = hidUsageCode)

    private fun macroKey(id: String, label: String, macroId: String) = KeyModel(
        id = id, label = label, action = KeyAction.MACRO, macroId = macroId
    )

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            macroKey("g1", "G1", "gaming_g1"),
            macroKey("g2", "G2", "gaming_g2"),
            macroKey("g3", "G3", "gaming_g3"),
            macroKey("g4", "G4", "gaming_g4"),
            macroKey("g5", "G5", "gaming_g5"),
            macroKey("g6", "G6", "gaming_g6")
        ),
        listOf(
            *"1234567890".mapIndexed { i, d ->
                KeyModel("n$d", d.toString(), hidUsageCode = if (d == '0') 0x27 else 0x1E + i)
            }.toTypedArray()
        ),
        listOf(
            letter("q", "Q", 0x14), letter("w", "W", 0x1A), letter("e", "E", 0x08), letter("r", "R", 0x15),
            letter("t", "T", 0x17), letter("y", "Y", 0x1C), letter("u", "U", 0x18), letter("i", "I", 0x0C),
            letter("o", "O", 0x12), letter("p", "P", 0x13)
        ),
        listOf(
            letter("a", "A", 0x04), letter("s", "S", 0x16), letter("d", "D", 0x07), letter("f", "F", 0x09),
            letter("g", "G", 0x0A), letter("h", "H", 0x0B), letter("j", "J", 0x0D), letter("k", "K", 0x0E),
            letter("l", "L", 0x0F)
        ),
        listOf(
            KeyModel("shift_l", "SHIFT", modifierBit = HidModifiers.LEFT_SHIFT, widthWeight = 1.6f),
            letter("z", "Z", 0x1D), letter("x", "X", 0x1B), letter("c", "C", 0x06), letter("v", "V", 0x19),
            letter("b", "B", 0x05), letter("n", "N", 0x11), letter("m", "M", 0x10),
            KeyModel("backspace", "⌫", hidUsageCode = 0x2A, widthWeight = 1.6f)
        ),
        listOf(
            KeyModel("ctrl_l", "CTRL", modifierBit = HidModifiers.LEFT_CTRL, widthWeight = 1.3f),
            KeyModel("alt_l", "ALT", modifierBit = HidModifiers.LEFT_ALT, widthWeight = 1.3f),
            KeyModel("space", "SPACE", hidUsageCode = 0x2C, widthWeight = 5f),
            KeyModel("fn", "FN", widthWeight = 1.3f)
        )
    )
}
