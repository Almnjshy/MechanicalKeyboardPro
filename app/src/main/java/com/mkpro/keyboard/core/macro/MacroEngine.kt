package com.mkpro.keyboard.core.macro

import com.mkpro.keyboard.core.connection.ConnectionManager
import com.mkpro.keyboard.core.connection.hid.HidReportBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores user-defined macros and plays them back by driving the active
 * ConnectionManager transport. Recording (capturing live key/mouse events
 * into a Macro) is exposed via startRecording/stopRecording; the actual
 * event capture hook wires in wherever raw input events are produced.
 */
class MacroEngine(private val connectionManager: ConnectionManager) {

    private val _macros = MutableStateFlow<List<Macro>>(emptyList())
    val macros: StateFlow<List<Macro>> = _macros.asStateFlow()

    private var recordingBuffer: MutableList<MacroStep>? = null

    fun save(macro: Macro) {
        _macros.value = _macros.value.filterNot { it.id == macro.id } + macro
    }

    fun delete(macroId: String) {
        _macros.value = _macros.value.filterNot { it.id == macroId }
    }

    fun startRecording() {
        recordingBuffer = mutableListOf()
    }

    fun captureStep(step: MacroStep) {
        recordingBuffer?.add(step)
    }

    fun stopRecording(name: String): Macro? {
        val steps = recordingBuffer ?: return null
        recordingBuffer = null
        val macro = Macro(id = name.lowercase().replace(" ", "_"), name = name, steps = steps)
        save(macro)
        return macro
    }

    suspend fun run(macroId: String) {
        val macro = _macros.value.find { it.id == macroId } ?: return
        executeSteps(macro.steps)
    }

    private suspend fun executeSteps(steps: List<MacroStep>) {
        for (step in steps) {
            when (step) {
                is MacroStep.KeyPress -> {
                    connectionManager.sendKeyEvent(HidReportBuilder.build(0, listOf(step.hidUsageCode)))
                    connectionManager.sendKeyEvent(HidReportBuilder.buildKeyUp())
                }
                is MacroStep.KeyCombo -> {
                    connectionManager.sendKeyEvent(HidReportBuilder.build(0, step.hidUsageCodes))
                    connectionManager.sendKeyEvent(HidReportBuilder.buildKeyUp())
                }
                is MacroStep.Delay -> delay(step.milliseconds)
                is MacroStep.Repeat -> repeat(step.times) { executeSteps(step.steps) }
                is MacroStep.MouseClick -> Unit // TODO: mouse HID report
                is MacroStep.LaunchApp -> Unit // TODO: remote "launch app" command frame
                is MacroStep.RunScript -> Unit // TODO: remote "run script" command frame
            }
        }
    }
}
