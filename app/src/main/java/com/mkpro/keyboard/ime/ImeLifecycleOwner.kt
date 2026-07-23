package com.mkpro.keyboard.ime

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/**
 * InputMethodService is not a LifecycleOwner/ViewModelStoreOwner/
 * SavedStateRegistryOwner the way an Activity or Fragment is, so a
 * ComposeView hosted inside onCreateInputView() needs one manually. This
 * mirrors the pattern used by every Compose-based Android keyboard: drive
 * the lifecycle state from the IME's own callbacks.
 *
 * Proper lifecycle flow:
 *   onCreate() -> ON_CREATE
 *   onStartInput() -> ON_START
 *   onStartInputView() -> ON_RESUME
 *   onFinishInputView() -> ON_PAUSE
 *   onFinishInput() -> ON_STOP
 *   onDestroy() -> ON_DESTROY
 */
class ImeLifecycleOwner : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore = ViewModelStore()
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    fun performRestore() {
        savedStateRegistryController.performRestore(null)
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    /** Convenience: move to a specific state ensuring proper transitions */
    fun moveToState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }
}
