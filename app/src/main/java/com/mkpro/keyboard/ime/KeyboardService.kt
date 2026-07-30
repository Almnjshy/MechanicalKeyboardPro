package com.mkpro.keyboard.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mkpro.keyboard.EXTRA_OPEN_ROUTE
import com.mkpro.keyboard.MainActivity
import com.mkpro.keyboard.MkProApplication
import com.mkpro.keyboard.core.connection.hid.HidModifiers
import com.mkpro.keyboard.core.keyboard.KeyAction
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.layers.LayerManager
import com.mkpro.keyboard.core.macro.Macro
import com.mkpro.keyboard.core.macro.MacroEngine
import com.mkpro.keyboard.core.macro.MacroStep
import com.mkpro.keyboard.core.settings.AppSettings
import com.mkpro.keyboard.ui.navigation.Screen
import com.mkpro.keyboard.ui.screens.keyboard.KeyboardIme
import com.mkpro.keyboard.ui.theme.MechanicalKeyboardProTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * The actual Android system keyboard. This is now the primary product per
 * the corrected spec - it types into ANY focused text field in ANY app,
 * with no computer connection required. The optional PC-connection mode
 * (ConnectionScreen/KeyboardScreen/BluetoothHidTransport, all still intact)
 * is a secondary feature reachable from the companion app's settings.
 */
class KeyboardService : InputMethodService() {

    /**
     * Never use fullscreen "extract" mode - always show as a normal docked
     * keyboard panel. Left at the default, some configurations (short
     * screens, landscape, certain OEM skins) can trigger InputMethodService's
     * built-in fullscreen editing UI, which behaves like a different window
     * mode entirely and is very likely what produced the "keyboard appears
     * from below the nav buttons instead of above them" symptom reported.
     */
    override fun onEvaluateFullscreenMode(): Boolean = false

    private val lifecycleOwner = ImeLifecycleOwner()
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val layerManager = LayerManager()
    private val macroEngine = MacroEngine(ImeMacroSink(currentInputConnection = { currentInputConnection }))

    // Sticky modifier state (Shift/Ctrl/Alt/Win) shared across all layers.
    private var heldModifierKeyIds by mutableStateOf(setOf<String>())
    private var capsLockOn by mutableStateOf(false)
    private var isAdvancedPanelExpanded by mutableStateOf(false)
    private var currentSettings = AppSettings()

    override fun onCreate() {
        super.onCreate()
        lifecycleOwner.performRestore()
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        registerBuiltInMacros()
        serviceScope.launch {
            (applicationContext as MkProApplication).settingsRepository.settingsFlow.collect { currentSettings = it }
        }
    }

    /** Ready-made macros for the Macros layer's M1-M4 slots (spec examples: Ctrl+C, Ctrl+V, Alt+Tab). */
    private fun registerBuiltInMacros() {
        val ctrl = com.mkpro.keyboard.core.connection.hid.HidModifierUsageCodes.LEFT_CTRL
        val alt = com.mkpro.keyboard.core.connection.hid.HidModifierUsageCodes.LEFT_ALT
        macroEngine.save(Macro("copy", "Copy", listOf(MacroStep.KeyCombo(listOf(ctrl, 0x06)))))   // Ctrl+C (C = 0x06)
        macroEngine.save(Macro("paste", "Paste", listOf(MacroStep.KeyCombo(listOf(ctrl, 0x19))))) // Ctrl+V (V = 0x19)
        macroEngine.save(Macro("cut", "Cut", listOf(MacroStep.KeyCombo(listOf(ctrl, 0x1B)))))     // Ctrl+X (X = 0x1B)
        macroEngine.save(Macro("alt_tab", "Alt+Tab", listOf(MacroStep.KeyCombo(listOf(alt, 0x2B))))) // Alt+Tab (Tab = 0x2B)
    }

    override fun onCreateInputView(): View {
        return try {
            val view = buildKeyboardView()
            attachOwnersToWindow()
            view
        } catch (t: Throwable) {
            Log.e("MechanicalKeyboardPro", "onCreateInputView failed", t)
            errorView(t)
        }
    }

    /**
     * Sets ViewTreeLifecycleOwner AND ViewTreeSavedStateRegistryOwner on
     * this service's actual Window decorView, not just on the ComposeView
     * we return from onCreateInputView(). Both are required unconditionally
     * by androidx.compose.ui.platform.AndroidComposeView.onAttachedToWindow()
     * - confirmed by two separate crashes caught by CrashReporter (missing
     * LifecycleOwner, then missing SavedStateRegistryOwner after the first
     * fix landed).
     *
     * InputMethodService manages its own Window (a Dialog-derived
     * SoftInputWindow), and some OEM builds (this was hit on what looks
     * like a MIUI device) insert additional framework container views
     * between that Window's real decorView and the View we return here.
     * Compose's ViewTreeLifecycleOwner.get()/ViewTreeSavedStateRegistryOwner
     * .get() walk UP from whatever view needs them toward the window root -
     * if that walk reaches one of those OEM-inserted containers before
     * reaching our ComposeView, it never sees the owners we set on
     * composeView alone. Setting them directly on window.decorView closes
     * that gap for every view in the window, regardless of who added it.
     */
    private fun attachOwnersToWindow() {
        val decorView = window?.window?.decorView ?: return
        decorView.setViewTreeLifecycleOwner(lifecycleOwner)
        decorView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
    }

    private fun buildKeyboardView(): View {
        val composeView = ComposeView(this)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        composeView.setContent {
            val settingsRepository = (applicationContext as MkProApplication).settingsRepository
            val settings by settingsRepository.settingsFlow.collectAsState(initial = AppSettings())

            MechanicalKeyboardProTheme(themeVariant = settings.theme) {
                val layers by layerManager.layers.collectAsState()
                val activeLayerId by layerManager.activeLayerId.collectAsState()
                val activeLayer = layers.firstOrNull { it.id == activeLayerId }

                KeyboardIme(
                    rows = activeLayer?.rows.orEmpty(),
                    activeLayerId = activeLayerId,
                    activeLayerName = activeLayer?.name ?: "",
                    heldModifierKeyIds = heldModifierKeyIds,
                    capsLockOn = capsLockOn,
                    isConnected = isPcConnected(),
                    isAdvancedPanelExpanded = isAdvancedPanelExpanded,
                    onKeyPressed = ::onKeyPressed,
                    onToggleAdvancedPanel = { isAdvancedPanelExpanded = !isAdvancedPanelExpanded },
                    onSwitchLayer = layerManager::switchTo,
                    onCycleLanguage = layerManager::cycleLanguage,
                    availableLayers = layers,
                    appSettings = settings,
                    onOpenSettings = { openCompanionAppScreen(Screen.Settings.route) }
                )
            }
        }
        return composeView
    }

    /** Launches MainActivity and deep-links straight to the given route (e.g. Settings) instead of Splash/Home. */
    private fun openCompanionAppScreen(route: String) {
        val intent = android.content.Intent(this, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(EXTRA_OPEN_ROUTE, route)
        }
        startActivity(intent)
    }

    /**
     * Plain android.widget.TextView, deliberately NOT Compose - if Compose
     * itself is what failed to build, falling back to another Compose view
     * would just fail the same way. This only covers synchronous failures
     * during view construction; CrashReporter (installed in
     * MkProApplication) covers everything else, including Compose's async
     * first draw/layout which happens after this function returns.
     */
    private fun errorView(t: Throwable): View = TextView(this).apply {
        text = "Mechanical Keyboard Pro failed to load:\n${t::class.java.simpleName}: ${t.message}"
        setPadding(24, 24, 24, 24)
        setBackgroundColor(0xFF1A1A1A.toInt())
        setTextColor(0xFFFFFFFF.toInt())
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        attachOwnersToWindow() // defensive: the window/decorView can be recreated between sessions
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onDestroy() {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun isPcConnected(): Boolean =
        (applicationContext as? MkProApplication)?.connectionManager?.connectionState?.value?.isConnected ?: false

    /**
     * Real sound + haptic feedback, driven by the Settings screen's toggles
     * (currentSettings, refreshed live from SettingsRepository). Uses the
     * system's built-in key-click sound effect rather than bundling an audio
     * asset, and a short one-shot vibration - the same approach every stock
     * Android keyboard uses for this.
     */
    private fun playKeyFeedback() {
        if (currentSettings.soundEnabled) {
            runCatching {
                val audioManager = getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
                audioManager.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD)
            }
        }
        if (currentSettings.vibrationEnabled) {
            runCatching {
                val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    val vibratorManager = getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                }
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(12, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    // --- Key dispatch -------------------------------------------------

    private fun onKeyPressed(key: KeyModel) {
        playKeyFeedback()

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
            KeyAction.MACRO -> key.macroId?.let { id -> serviceScope.launch { macroEngine.run(id) } }
            KeyAction.LAYER_SWITCH, KeyAction.SYSTEM_COMMAND -> Unit // TODO: wire system command dispatch
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
        val code = AndroidKeyCodeRegistry.resolve(keyCodeName) ?: return
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
