package com.mkpro.keyboard.domain.usecase

import com.mkpro.keyboard.core.macro.MacroEngine

/**
 * Example domain use case: business logic that coordinates more than one
 * core/ module belongs here, not in a ViewModel or a core/ manager itself.
 */
class RunMacroUseCase(private val macroEngine: MacroEngine) {
    suspend operator fun invoke(macroId: String) {
        macroEngine.run(macroId)
    }
}
