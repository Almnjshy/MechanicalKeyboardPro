package com.mkpro.keyboard.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mkpro.keyboard.MkProApplication
import com.mkpro.keyboard.core.connection.hid.HidModifiers
import com.mkpro.keyboard.core.keyboard.KeyAction
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.layers.LayerManager
import com.mkpro.keyboard.ui.screens.keyboard.KeyboardIme
import com.mkpro.keyboard.ui.theme.MechanicalKeyboardProTheme

/**
 * The actual Android system keyboard. This is now the primary product per
 * the corrected spec - it types into ANY focused text field in ANY app,
 * with no computer connection required. The optional PC-connection mode
 * (ConnectionScreen/KeyboardScreen/BluetoothHidTransport, all still intact)
 * is a secondary feature reachable from the companion app's settings.
 */
class KeyboardService : InputMethodService() {

    private val lifecycleOwner = ImeLifecycleOwner()
    private val layerManager = LayerManager()

    // Sticky modifier state (Shift/Ctrl/Alt/Win) shared across all layers.
    private var heldModifierKeyIds by mutableStateOf(setOf<String>())
    private var capsLockOn by mutableStateOf(false)
    private var isAdvancedPanelExpanded by mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()
        lifecycleOwner.performRestore()
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        composeView.setContent {
            MechanicalKeyboardProTheme {
                val layers by layerManager.layers.collectAsState()
                val activeLayerId by layerManager.activeLayerId.collectAsState()
                val activeLayer = layers.firstOrNull { it.id == activeLayerId }

                KeyboardIme(
                    rows = activeLayer?.rows.orEmpty(),
                    activeLayerName = activeLayer?.name ?: "",
                    heldModifierKeyIds = heldModifierKeyIds,
                    capsLockOn = capsLockOn,
                    isConnected = isPcConnected(),
                    isAdvancedPanelExpanded = isAdvancedPanelExpanded,
                    onKeyPressed = ::onKeyPressed,
                    onToggleAdvancedPanel = { isAdvancedPanelExpanded = !isAdvancedPanelExpanded },
                    onSwitchLayer = layerManager::switchTo,
                    availableLayers = layers
                )
            }
        }
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onDestroy() {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    private fun isPcConnected(): Boolean =
        (applicationContext as? MkProApplication)?.connectionManager?.connectionState?.value?.isConnected ?: false

    // --- Key dispatch -------------------------------------------------

    private fun onKeyPressed(key: KeyModel) {
        // Modifier keys (Shift/Ctrl/Alt/Win) toggle sticky state; they never
        // produce input on their own.
        if (key.modifierBit != null) {
            heldModifierKeyIds = if (key.id in heldModifierKeyIds) heldModifierKeyIds - key.id else heldModifierKeyIds + key.id
            return
        }

        when (key.id) {
            "lang_switch" -> { layerManager.cycleLanguage(); return }
            "caps" -> { capsLockOn = !capsLockOn; return }
        }

        when (key.action) {
            KeyAction.LANGUAGE_SWITCH -> layerManager.cycleLanguage()
            KeyAction.TEXT_INSERT -> commitLetter(key.command.orEmpty())
            KeyAction.PC_KEY_EVENT -> sendAndroidKeyEvent(key.command, consumeModifiers())
            KeyAction.HID_KEY -> dispatchStandardKey(key)
            KeyAction.MACRO, KeyAction.LAYER_SWITCH, KeyAction.SYSTEM_COMMAND -> Unit // TODO: wire macro/system command dispatch
        }
    }

    /** Applies caps-lock/one-shot-shift casing, then clears any one-shot Shift. */
    private fun commitLetter(text: String) {
        val shiftHeld = heldModifierKeyIds.any { it == "shift_l" || it == "shift_r" }
        val shouldUppercase = capsLockOn xor shiftHeld
        currentInputConnection?.commitText(if (shouldUppercase) text.uppercase() else text.lowercase(), 1)
        if (shiftHeld) heldModifierKeyIds = heldModifierKeyIds - "shift_l" - "shift_r"
    }

    private fun dispatchStandardKey(key: KeyModel) {
        when (key.id) {
            "space" -> { currentInputConnection?.commitText(" ", 1); return }
            "enter" -> { currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)); return }
            "backspace" -> { currentInputConnection?.deleteSurroundingText(1, 0); return }
            "tab" -> { currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB)); return }
            "esc" -> return // no-op on phone typing; meaningful only in PC-connected mode
        }

        val activeLayerKeys = layerManager.layers.value
            .firstOrNull { it.id == layerManager.activeLayerId.value }?.rows.orEmpty().flatten()
        val nonShiftMask = heldModifierKeyIds
            .mapNotNull { id -> activeLayerKeys.firstOrNull { it.id == id }?.modifierBit }
            .filterNot { it == HidModifiers.LEFT_SHIFT || it == HidModifiers.RIGHT_SHIFT }
            .fold(0) { acc, bit -> acc or bit }

        val letterOrDigit = key.label.singleOrNull()?.let { androidKeyCodeForChar(it) }

        if (nonShiftMask != 0 && letterOrDigit != null) {
            // Ctrl/Alt/Win + key -> shortcut, e.g. Ctrl+C in a terminal app.
            var metaState = 0
            if (nonShiftMask and (HidModifiers.LEFT_CTRL or HidModifiers.RIGHT_CTRL) != 0) metaState = metaState or KeyEvent.META_CTRL_ON
            if (nonShiftMask and (HidModifiers.LEFT_ALT or HidModifiers.RIGHT_ALT) != 0) metaState = metaState or KeyEvent.META_ALT_ON
            if (nonShiftMask and (HidModifiers.LEFT_GUI or HidModifiers.RIGHT_GUI) != 0) metaState = metaState or KeyEvent.META_META_ON
            currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, letterOrDigit, 0, metaState))
            heldModifierKeyIds = emptySet() // combo consumed, including any Shift held alongside it
            return
        }

        if (key.label.length == 1 && key.label.first().isLetter()) {
            commitLetter(key.label)
        } else if (key.label.length == 1) {
            currentInputConnection?.commitText(key.label, 1)
        }
    }

    private fun sendAndroidKeyEvent(keyCodeName: String?, modifierMask: Int) {
        val code = keyCodeName?.let { runCatching { KeyEvent::class.java.getField(it).getInt(null) }.getOrNull() } ?: return
        var metaState = 0
        if (modifierMask and (HidModifiers.LEFT_CTRL or HidModifiers.RIGHT_CTRL) != 0) metaState = metaState or KeyEvent.META_CTRL_ON
        if (modifierMask and (HidModifiers.LEFT_ALT or HidModifiers.RIGHT_ALT) != 0) metaState = metaState or KeyEvent.META_ALT_ON
        if (modifierMask and (HidModifiers.LEFT_GUI or HidModifiers.RIGHT_GUI) != 0) metaState = metaState or KeyEvent.META_META_ON
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, code, 0, metaState))
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, code, 0, metaState))
    }

    /** Reads the currently-held modifier bits, then clears them (Shift/Ctrl/Alt/Win are all one-shot here). */
    private fun consumeModifiers(): Int {
        val activeLayer = layerManager.layers.value.firstOrNull { it.id == layerManager.activeLayerId.value }
        val allKeys = activeLayer?.rows.orEmpty().flatten()
        val mask = heldModifierKeyIds.mapNotNull { id -> allKeys.firstOrNull { it.id == id }?.modifierBit }
            .fold(0) { acc, bit -> acc or bit }
        if (heldModifierKeyIds.isNotEmpty()) heldModifierKeyIds = emptySet()
        return mask
    }

    private fun androidKeyCodeForChar(char: Char): Int? = when {
        char.isLetter() -> KeyEvent.KEYCODE_A + (char.uppercaseChar() - 'A')
        char.isDigit() -> if (char == '0') KeyEvent.KEYCODE_0 else KeyEvent.KEYCODE_0 + (char - '0')
        else -> null
    }
}
