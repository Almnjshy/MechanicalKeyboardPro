package com.mkpro.keyboard.core.keyboard

/**
 * Standard Arabic keyboard layout (same physical row shape as StandardLayout,
 * Arabic letters mapped to their conventional key positions). Unlike the
 * English layer, these keys mostly insert Unicode text directly (action =
 * TEXT_INSERT) rather than USB HID usage codes, since the primary output
 * path is now InputMethodService.commitText - HID codes only matter for the
 * optional PC-connection mode and don't cover Arabic glyphs anyway.
 */
object ArabicLayout {

    private fun charKey(id: String, char: String, widthWeight: Float = 1f) = KeyModel(
        id = id, label = char, widthWeight = widthWeight,
        action = KeyAction.TEXT_INSERT,
        command = char
    )

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel("esc", "ESC"),
            *(1..12).map { KeyModel("f$it", "F$it") }.toTypedArray()
        ),
        listOf(
            charKey("grave", "ذ"),
            charKey("n1", "١"), charKey("n2", "٢"), charKey("n3", "٣"), charKey("n4", "٤"), charKey("n5", "٥"),
            charKey("n6", "٦"), charKey("n7", "٧"), charKey("n8", "٨"), charKey("n9", "٩"), charKey("n0", "٠"),
            charKey("minus", "-"), charKey("equal", "="),
            KeyModel("backspace", "⌫", widthWeight = 2f)
        ),
        listOf(
            KeyModel("tab", "TAB", widthWeight = 1.5f),
            charKey("q", "ض"), charKey("w", "ص"), charKey("e", "ث"), charKey("r", "ق"), charKey("t", "ف"),
            charKey("y", "غ"), charKey("u", "ع"), charKey("i", "ه"), charKey("o", "خ"), charKey("p", "ح"),
            charKey("bracket_l", "ج"), charKey("bracket_r", "د")
        ),
        listOf(
            KeyModel("caps", "CAPS", widthWeight = 1.75f),
            charKey("a", "ش"), charKey("s", "س"), charKey("d", "ي"), charKey("f", "ب"), charKey("g", "ل"),
            charKey("h", "ا"), charKey("j", "ت"), charKey("k", "ن"), charKey("l", "م"),
            charKey("semicolon", "ك"), charKey("quote", "ط"),
            KeyModel("enter", "ENTER", widthWeight = 2f)
        ),
        listOf(
            KeyModel("shift_l", "SHIFT", widthWeight = 2.25f),
            charKey("z", "ئ"), charKey("x", "ء"), charKey("c", "ؤ"), charKey("v", "ر"), charKey("b", "لا"),
            charKey("n", "ى"), charKey("m", "ة"),
            charKey("comma", "و"), charKey("period", "ز"), charKey("slash", "ظ"),
            KeyModel("shift_r", "SHIFT", widthWeight = 2.25f)
        ),
        listOf(
            KeyModel("ctrl_l", "CTRL", widthWeight = 1.25f),
            KeyModel("win", "WIN", widthWeight = 1.25f),
            KeyModel("alt_l", "ALT", widthWeight = 1.25f),
            charKey("space", "مسافة", widthWeight = 5f),
            KeyModel("lang_switch", "🌐", action = KeyAction.LANGUAGE_SWITCH, widthWeight = 1f),
            KeyModel("alt_r", "ALT", widthWeight = 1.25f),
            KeyModel("fn", "FN", widthWeight = 1.25f),
            KeyModel("ctrl_r", "CTRL", widthWeight = 1.25f)
        )
    )
}
