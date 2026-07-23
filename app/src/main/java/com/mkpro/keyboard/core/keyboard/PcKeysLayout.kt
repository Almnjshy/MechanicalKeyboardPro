package com.mkpro.keyboard.core.keyboard

import com.mkpro.keyboard.core.connection.hid.HidModifiers

/**
 * "PC keys" layer from the corrected spec: desktop keys normally missing on
 * mobile (ESC/TAB/CTRL/ALT/WIN/F1-F24/arrows/HOME/END/INSERT/DELETE/
 * PAGE UP/PAGE DOWN/PRINT SCREEN/SCROLL LOCK/PAUSE). Every key is tagged
 * action = PC_KEY_EVENT with `command` holding the target
 * android.view.KeyEvent.KEYCODE_* name, resolved by KeyboardService via
 * InputConnection.sendKeyEvent - see KeyboardService.androidKeyCodeFor().
 */
object PcKeysLayout {

    private fun pcKey(id: String, label: String, keyCodeName: String, widthWeight: Float = 1f) =
        KeyModel(id = id, label = label, widthWeight = widthWeight, action = KeyAction.PC_KEY_EVENT, command = keyCodeName)

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            pcKey("esc", "ESC", "KEYCODE_ESCAPE"),
            *(1..12).map { pcKey("f$it", "F$it", "KEYCODE_F$it") }.toTypedArray()
        ),
        listOf(
            pcKey("print_screen", "PRT SC", "KEYCODE_SYSRQ"),
            pcKey("scroll_lock", "SCR LK", "KEYCODE_SCROLL_LOCK"),
            pcKey("pause_break", "PAUSE", "KEYCODE_BREAK"),
            pcKey("insert", "INS", "KEYCODE_INSERT"),
            pcKey("delete", "DEL", "KEYCODE_FORWARD_DEL", widthWeight = 1.2f),
            pcKey("home", "HOME", "KEYCODE_MOVE_HOME"),
            pcKey("end", "END", "KEYCODE_MOVE_END"),
            pcKey("page_up", "PG UP", "KEYCODE_PAGE_UP"),
            pcKey("page_down", "PG DN", "KEYCODE_PAGE_DOWN")
        ),
        listOf(
            pcKey("tab", "TAB", "KEYCODE_TAB", widthWeight = 1.5f),
            KeyModel("ctrl_l", "CTRL", modifierBit = HidModifiers.LEFT_CTRL),
            KeyModel("alt_l", "ALT", modifierBit = HidModifiers.LEFT_ALT),
            KeyModel("win", "WIN", modifierBit = HidModifiers.LEFT_GUI),
            pcKey("menu", "MENU", "KEYCODE_MENU"),
            KeyModel("fn", "FN", widthWeight = 1f)
        ),
        listOf(
            pcKey("arrow_up", "▲", "KEYCODE_DPAD_UP"),
            pcKey("space_pc", "SPACE", "KEYCODE_SPACE", widthWeight = 4f),
            pcKey("arrow_down", "▼", "KEYCODE_DPAD_DOWN")
        ),
        listOf(
            pcKey("arrow_left", "◀", "KEYCODE_DPAD_LEFT", widthWeight = 2f),
            pcKey("arrow_right", "▶", "KEYCODE_DPAD_RIGHT", widthWeight = 2f)
        )
    )
}
