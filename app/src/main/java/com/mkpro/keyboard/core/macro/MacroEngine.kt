package com.mkpro.keyboard.core.macro

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores user-defined macros and plays them back through whichever
 * MacroSink it's given - ImeMacroSink for normal phone typing (the default)
 * or BluetoothMacroSink when PC mode is active. The engine itself has no
 * idea which one it's talking to.
 */
class MacroEngine(private val sink: MacroSink) {

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
                is MacroStep.KeyPress -> sink.pressKey(step.hidUsageCode)
                is MacroStep.KeyCombo -> sink.pressCombo(step.hidUsageCodes)
                is MacroStep.TextInsert -> sink.insertText(step.text)
                is MacroStep.Delay -> delay(step.milliseconds)
                is MacroStep.Repeat -> repeat(step.times) { executeSteps(step.steps) }
                is MacroStep.MouseClick -> sink.mouseClick(step.button)
                is MacroStep.LaunchApp -> sink.launchApp(step.packageName)
                is MacroStep.RunScript -> sink.runScript(step.scriptPath)
            }
        }
    }
}
