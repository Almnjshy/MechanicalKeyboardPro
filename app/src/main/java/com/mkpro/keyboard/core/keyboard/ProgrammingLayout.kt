package com.mkpro.keyboard.core.keyboard

import com.mkpro.keyboard.core.connection.hid.HidModifiers

/**
 * "Programming" layer: a dedicated symbol row (braces/brackets/parens/
 * comparison/quotes/pipe - the punctuation used constantly in code but
 * buried 2-3 taps deep in a stock mobile keyboard) plus F-keys up top,
 * with a full QWERTY typing area underneath so this layer is still usable
 * for actually writing code/comments, not just inserting symbols.
 */
object ProgrammingLayout {

    private fun symbol(id: String, text: String) = KeyModel(
        id = id, label = text, action = KeyAction.TEXT_INSERT, command = text
    )

    private fun letter(id: String, label: String, hidUsageCode: Int) =
        KeyModel(id = id, label = label, hidUsageCode = hidUsageCode)

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            symbol("brace_l", "{"), symbol("brace_r", "}"),
            symbol("bracket_l", "["), symbol("bracket_r", "]"),
            symbol("paren_l", "("), symbol("paren_r", ")"),
            symbol("angle_l", "<"), symbol("angle_r", ">"),
            symbol("equal", "="), symbol("semicolon", ";"),
            symbol("colon", ":"), symbol("dquote", "\""),
            symbol("quote", "'"), symbol("pipe", "|"), symbol("question", "?")
        ),
        listOf(*(1..12).map { KeyModel("f$it", "F$it") }.toTypedArray()),
        listOf(
            KeyModel("tab", "TAB", hidUsageCode = 0x2B, widthWeight = 1.5f),
            letter("q", "Q", 0x14), letter("w", "W", 0x1A), letter("e", "E", 0x08), letter("r", "R", 0x15),
            letter("t", "T", 0x17), letter("y", "Y", 0x1C), letter("u", "U", 0x18), letter("i", "I", 0x0C),
            letter("o", "O", 0x12), letter("p", "P", 0x13)
        ),
        listOf(
            letter("a", "A", 0x04), letter("s", "S", 0x16), letter("d", "D", 0x07), letter("f", "F", 0x09),
            letter("g", "G", 0x0A), letter("h", "H", 0x0B), letter("j", "J", 0x0D), letter("k", "K", 0x0E),
            letter("l", "L", 0x0F),
            KeyModel("enter", "ENTER", hidUsageCode = 0x28, widthWeight = 1.5f)
        ),
        listOf(
            KeyModel("shift_l", "SHIFT", modifierBit = HidModifiers.LEFT_SHIFT, widthWeight = 1.5f),
            letter("z", "Z", 0x1D), letter("x", "X", 0x1B), letter("c", "C", 0x06), letter("v", "V", 0x19),
            letter("b", "B", 0x05), letter("n", "N", 0x11), letter("m", "M", 0x10),
            symbol("comma", ","), symbol("period", "."), symbol("slash", "/"),
            KeyModel("backspace", "⌫", hidUsageCode = 0x2A, widthWeight = 1.5f)
        ),
        listOf(
            KeyModel("ctrl_l", "CTRL", modifierBit = HidModifiers.LEFT_CTRL, widthWeight = 1.3f),
            KeyModel("alt_l", "ALT", modifierBit = HidModifiers.LEFT_ALT, widthWeight = 1.3f),
            KeyModel("space", "SPACE", hidUsageCode = 0x2C, widthWeight = 5f),
            KeyModel("fn", "FN", widthWeight = 1.3f)
        )
    )
}
