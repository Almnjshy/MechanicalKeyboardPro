package com.mkpro.keyboard.core.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            language = prefs[Keys.LANGUAGE] ?: "ar",
            keyboardOpacity = prefs[Keys.OPACITY] ?: 1f,
            soundEnabled = prefs[Keys.SOUND] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION] ?: true,
            theme = prefs[Keys.THEME]?.let { runCatching { ThemeVariant.valueOf(it) }.getOrNull() }
                ?: ThemeVariant.DARK
        )
    }

    suspend fun update(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = settings.language
            prefs[Keys.OPACITY] = settings.keyboardOpacity
            prefs[Keys.SOUND] = settings.soundEnabled
            prefs[Keys.VIBRATION] = settings.vibrationEnabled
            prefs[Keys.THEME] = settings.theme.name
        }
    }
}
