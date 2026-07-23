package com.mkpro.keyboard.core.macro

sealed class MacroStep {
    data class KeyPress(val hidUsageCode: Int) : MacroStep()
    data class KeyCombo(val hidUsageCodes: List<Int>) : MacroStep()
    data class Delay(val milliseconds: Long) : MacroStep()
    data class MouseClick(val button: Int) : MacroStep()
    data class LaunchApp(val packageName: String) : MacroStep()
    data class RunScript(val scriptPath: String) : MacroStep()
    data class Repeat(val steps: List<MacroStep>, val times: Int) : MacroStep()
}

data class Macro(
    val id: String,
    val name: String,
    val steps: List<MacroStep>
)
