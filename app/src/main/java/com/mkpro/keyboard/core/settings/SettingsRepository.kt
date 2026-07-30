package com.mkpro.keyboard.core.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mkpro.keyboard.core.rgb.RgbEffectType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mkpro_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val OPACITY = floatPreferencesKey("keyboard_opacity")
        val SOUND = booleanPreferencesKey("sound_enabled")
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
        val THEME = stringPreferencesKey("theme")
        val RGB_EFFECT = stringPreferencesKey("rgb_effect")
        val RGB_COLOR = intPreferencesKey("rgb_color_argb")
        val RGB_BRIGHTNESS = floatPreferencesKey("rgb_brightness")
        val RGB_SPEED = floatPreferencesKey("rgb_speed")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val defaults = AppSettings()
        AppSettings(
            language = prefs[Keys.LANGUAGE] ?: defaults.language,
            keyboardOpacity = prefs[Keys.OPACITY] ?: defaults.keyboardOpacity,
            soundEnabled = prefs[Keys.SOUND] ?: defaults.soundEnabled,
            vibrationEnabled = prefs[Keys.VIBRATION] ?: defaults.vibrationEnabled,
            theme = prefs[Keys.THEME]?.let { runCatching { ThemeVariant.valueOf(it) }.getOrNull() }
                ?: defaults.theme,
            rgbEffect = prefs[Keys.RGB_EFFECT]?.let { runCatching { RgbEffectType.valueOf(it) }.getOrNull() }
                ?: defaults.rgbEffect,
            rgbColorArgb = prefs[Keys.RGB_COLOR] ?: defaults.rgbColorArgb,
            rgbBrightness = prefs[Keys.RGB_BRIGHTNESS] ?: defaults.rgbBrightness,
            rgbSpeed = prefs[Keys.RGB_SPEED] ?: defaults.rgbSpeed
        )
    }

    suspend fun update(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = settings.language
            prefs[Keys.OPACITY] = settings.keyboardOpacity
            prefs[Keys.SOUND] = settings.soundEnabled
            prefs[Keys.VIBRATION] = settings.vibrationEnabled
            prefs[Keys.THEME] = settings.theme.name
            prefs[Keys.RGB_EFFECT] = settings.rgbEffect.name
            prefs[Keys.RGB_COLOR] = settings.rgbColorArgb
            prefs[Keys.RGB_BRIGHTNESS] = settings.rgbBrightness
            prefs[Keys.RGB_SPEED] = settings.rgbSpeed
        }
    }
}
