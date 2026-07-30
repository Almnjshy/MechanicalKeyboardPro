package com.mkpro.keyboard.core.keyboard

/**
 * "Macros" layer: each key triggers a saved Macro by id via KeyAction.MACRO
 * (dispatched in KeyboardService through MacroEngine.run(key.macroId)).
 * M1-M4 ship pre-registered with common shortcuts (Ctrl+C/V/X, Alt+Tab) -
 * see KeyboardService.registerBuiltInMacros(). M5-M8 are empty slots the
 * user assigns later via the (not yet built) macro recorder/designer UI.
 */
object MacroLayout {

    private fun macroKey(id: String, label: String, macroId: String) = KeyModel(
        id = id, label = label, action = KeyAction.MACRO, macroId = macroId, widthWeight = 1.5f
    )

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            macroKey("m1", "M1\nCOPY", "copy"),
            macroKey("m2", "M2\nPASTE", "paste"),
            macroKey("m3", "M3\nCUT", "cut"),
            macroKey("m4", "M4\nALT+TAB", "alt_tab")
        ),
        listOf(
            macroKey("m5", "M5", "custom_5"),
            macroKey("m6", "M6", "custom_6"),
            macroKey("m7", "M7", "custom_7"),
            macroKey("m8", "M8", "custom_8")
        ),
        listOf(
            KeyModel("backspace", "⌫", hidUsageCode = 0x2A, widthWeight = 3f),
            KeyModel("enter", "ENTER", hidUsageCode = 0x28, widthWeight = 3f)
        )
    )
}
