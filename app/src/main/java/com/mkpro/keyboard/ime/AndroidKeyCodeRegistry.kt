package com.mkpro.keyboard.ime

import android.view.KeyEvent

/**
 * Maps the KEYCODE_* name strings stored in PcKeysLayout's KeyModel.command
 * to real android.view.KeyEvent constants via a plain compile-time map
 * instead of reflection (KeyEvent::class.java.getField(...)). Reflection on
 * framework classes is fragile under R8/minification and slower for no
 * benefit here since the key set is small and fixed.
 */
object AndroidKeyCodeRegistry {

    private val codes: Map<String, Int> = mapOf(
        "KEYCODE_ESCAPE" to KeyEvent.KEYCODE_ESCAPE,
        "KEYCODE_F1" to KeyEvent.KEYCODE_F1,
        "KEYCODE_F2" to KeyEvent.KEYCODE_F2,
        "KEYCODE_F3" to KeyEvent.KEYCODE_F3,
        "KEYCODE_F4" to KeyEvent.KEYCODE_F4,
        "KEYCODE_F5" to KeyEvent.KEYCODE_F5,
        "KEYCODE_F6" to KeyEvent.KEYCODE_F6,
        "KEYCODE_F7" to KeyEvent.KEYCODE_F7,
        "KEYCODE_F8" to KeyEvent.KEYCODE_F8,
        "KEYCODE_F9" to KeyEvent.KEYCODE_F9,
        "KEYCODE_F10" to KeyEvent.KEYCODE_F10,
        "KEYCODE_F11" to KeyEvent.KEYCODE_F11,
        "KEYCODE_F12" to KeyEvent.KEYCODE_F12,
        "KEYCODE_SYSRQ" to KeyEvent.KEYCODE_SYSRQ,
        "KEYCODE_SCROLL_LOCK" to KeyEvent.KEYCODE_SCROLL_LOCK,
        "KEYCODE_BREAK" to KeyEvent.KEYCODE_BREAK,
        "KEYCODE_INSERT" to KeyEvent.KEYCODE_INSERT,
        "KEYCODE_FORWARD_DEL" to KeyEvent.KEYCODE_FORWARD_DEL,
        "KEYCODE_MOVE_HOME" to KeyEvent.KEYCODE_MOVE_HOME,
        "KEYCODE_MOVE_END" to KeyEvent.KEYCODE_MOVE_END,
        "KEYCODE_PAGE_UP" to KeyEvent.KEYCODE_PAGE_UP,
        "KEYCODE_PAGE_DOWN" to KeyEvent.KEYCODE_PAGE_DOWN,
        "KEYCODE_TAB" to KeyEvent.KEYCODE_TAB,
        "KEYCODE_MENU" to KeyEvent.KEYCODE_MENU,
        "KEYCODE_DPAD_UP" to KeyEvent.KEYCODE_DPAD_UP,
        "KEYCODE_DPAD_DOWN" to KeyEvent.KEYCODE_DPAD_DOWN,
        "KEYCODE_DPAD_LEFT" to KeyEvent.KEYCODE_DPAD_LEFT,
        "KEYCODE_DPAD_RIGHT" to KeyEvent.KEYCODE_DPAD_RIGHT,
        "KEYCODE_SPACE" to KeyEvent.KEYCODE_SPACE,
        "KEYCODE_ENTER" to KeyEvent.KEYCODE_ENTER,
        "KEYCODE_DEL" to KeyEvent.KEYCODE_DEL
    )

    fun resolve(keyCodeName: String?): Int? = keyCodeName?.let { codes[it] }
}
