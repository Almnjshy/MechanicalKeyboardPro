package com.mkpro.keyboard.ime

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/**
 * InputMethodService is neither a LifecycleOwner nor a
 * SavedStateRegistryOwner the way an Activity or Fragment is, so a
 * ComposeView hosted inside onCreateInputView() needs both supplied
 * manually - this is NOT optional: androidx.compose.ui.platform
 * .AndroidComposeView.onAttachedToWindow() unconditionally requires both
 * ViewTreeLifecycleOwner and ViewTreeSavedStateRegistryOwner to be present
 * the moment the view attaches to a window, regardless of whether the
 * app's own code calls rememberSaveable() or not. Omitting either throws
 * IllegalStateException immediately on attach - confirmed by two separate
 * crashes caught by CrashReporter (missing LifecycleOwner, then missing
 * SavedStateRegistryOwner after the first fix).
 *
 * ViewModelStoreOwner is genuinely optional here (only needed if viewModel()
 * is called, which this codebase doesn't do) and stays removed.
 */
class ImeLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    fun performRestore() {
        savedStateRegistryController.performRestore(null)
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }
}
