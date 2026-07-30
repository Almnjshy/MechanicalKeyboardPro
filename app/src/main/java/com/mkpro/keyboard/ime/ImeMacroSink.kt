package com.mkpro.keyboard.ime

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.mkpro.keyboard.core.macro.MacroSink

/**
 * Plays macros back by typing into whatever app currently has focus - the
 * primary-mode counterpart to BluetoothMacroSink. `hidUsageCode` values are
 * translated back to Android KeyEvent codes using the same USB HID Usage
 * Page 0x07 ranges StandardLayout assigns (0x04-0x1D = A-Z, 0x1E-0x27 =
 * 1-9,0), so a saved macro plays back the same way whether or not a PC is
 * connected.
 */
class ImeMacroSink(
    private val currentInputConnection: () -> InputConnection?,
    private val launchAppAction: (String) -> Unit = {}
) : MacroSink {

    override suspend fun pressKey(hidUsageCode: Int) {
        dispatch(hidUsageCode, 0)
    }

    override suspend fun pressCombo(hidUsageCodes: List<Int>) {
        // Every code except the last is treated as a held modifier;
        // callers building shortcuts (Ctrl+C, Alt+Tab...) should list
        // modifier usage codes (0xE0-0xE7) first, then the target key last.
        val metaState = hidUsageCodes.dropLast(1)
            .mapNotNull { modifierMetaStateFor(it) }
            .fold(0) { acc, bit -> acc or bit }
        hidUsageCodes.lastOrNull()?.let { dispatch(it, metaState) }
    }

    override suspend fun insertText(text: String) {
        currentInputConnection()?.commitText(text, 1)
    }

    override suspend fun mouseClick(button: Int) {
        // No system-wide "mouse click" concept for an IME typing into a
        // text field - meaningful only in PC-connected mode.
    }

    override suspend fun launchApp(packageName: String) {
        launchAppAction(packageName)
    }

    override suspend fun runScript(scriptPath: String) {
        // Not applicable to local phone typing.
    }

    private fun dispatch(hidUsageCode: Int, metaState: Int) {
        val androidKeyCode = androidKeyCodeForHidUsage(hidUsageCode) ?: return
        val ic = currentInputConnection() ?: return
        ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, androidKeyCode, 0, metaState))
        ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, androidKeyCode, 0, metaState))
    }

    private fun modifierMetaStateFor(hidUsageCode: Int): Int? = when (hidUsageCode) {
        0xE0, 0xE4 -> KeyEvent.META_CTRL_ON   // Left/Right Ctrl
        0xE1, 0xE5 -> KeyEvent.META_SHIFT_ON  // Left/Right Shift
        0xE2, 0xE6 -> KeyEvent.META_ALT_ON    // Left/Right Alt
        0xE3, 0xE7 -> KeyEvent.META_META_ON   // Left/Right GUI
        else -> null
    }

    private fun androidKeyCodeForHidUsage(code: Int): Int? = when (code) {
        in 0x04..0x1D -> KeyEvent.KEYCODE_A + (code - 0x04) // A-Z
        in 0x1E..0x26 -> KeyEvent.KEYCODE_1 + (code - 0x1E) // 1-9
        0x27 -> KeyEvent.KEYCODE_0
        0x28 -> KeyEvent.KEYCODE_ENTER
        0x29 -> KeyEvent.KEYCODE_ESCAPE
        0x2A -> KeyEvent.KEYCODE_DEL
        0x2B -> KeyEvent.KEYCODE_TAB
        0x2C -> KeyEvent.KEYCODE_SPACE
        else -> null
    }
}
