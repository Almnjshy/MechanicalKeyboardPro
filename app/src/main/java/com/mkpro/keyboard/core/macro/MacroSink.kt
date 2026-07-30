package com.mkpro.keyboard.core.macro

/**
 * Where a macro's steps actually go. Two implementations today:
 * - BluetoothMacroSink: drives the PC over BluetoothHidTransport (optional PC mode)
 * - ime.ImeMacroSink: drives InputConnection.commitText/sendKeyEvent (primary phone mode)
 *
 * MacroEngine only knows this contract, so the same saved Macro (e.g. a
 * text-expansion snippet or Ctrl+C) plays back correctly regardless of
 * which mode is active.
 */
interface MacroSink {
    suspend fun pressKey(hidUsageCode: Int)
    suspend fun pressCombo(hidUsageCodes: List<Int>)
    suspend fun insertText(text: String)
    suspend fun mouseClick(button: Int)
    suspend fun launchApp(packageName: String)
    suspend fun runScript(scriptPath: String)
}
