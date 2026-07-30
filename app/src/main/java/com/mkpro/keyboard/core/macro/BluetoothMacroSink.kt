package com.mkpro.keyboard.core.macro

import com.mkpro.keyboard.core.connection.ConnectionManager
import com.mkpro.keyboard.core.connection.hid.HidReportBuilder

/** Plays macros back over the active PC connection (Bluetooth HID today). */
class BluetoothMacroSink(private val connectionManager: ConnectionManager) : MacroSink {

    override suspend fun pressKey(hidUsageCode: Int) {
        connectionManager.sendKeyEvent(HidReportBuilder.build(0, listOf(hidUsageCode)))
        connectionManager.sendKeyEvent(HidReportBuilder.buildKeyUp())
    }

    override suspend fun pressCombo(hidUsageCodes: List<Int>) {
        connectionManager.sendKeyEvent(HidReportBuilder.build(0, hidUsageCodes))
        connectionManager.sendKeyEvent(HidReportBuilder.buildKeyUp())
    }

    override suspend fun insertText(text: String) {
        // No direct "type string" HID primitive - would need per-character
        // usage-code lookup. Left as a follow-up; PC mode macros should use
        // pressKey/pressCombo for now.
    }

    override suspend fun mouseClick(button: Int) {
        // TODO: mouse HID report (separate descriptor/collection from keyboard)
    }

    override suspend fun launchApp(packageName: String) {
        // TODO: remote "launch app" command frame to a desktop companion process
    }

    override suspend fun runScript(scriptPath: String) {
        // TODO: remote "run script" command frame
    }
}
