package com.mkpro.keyboard

import android.app.Application
import com.mkpro.keyboard.core.connection.ConnectionManager
import com.mkpro.keyboard.core.profiles.ProfileManager
import com.mkpro.keyboard.core.rgb.RgbController
import com.mkpro.keyboard.core.settings.SettingsRepository

/**
 * Application-wide singleton container.
 *
 * Kept as a lightweight manual DI container for now (no Hilt/Koin dependency),
 * so every core/ module is independently swappable without touching the UI layer.
 */
class MkProApplication : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var connectionManager: ConnectionManager
        private set

    lateinit var profileManager: ProfileManager
        private set

    lateinit var rgbController: RgbController
        private set

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        connectionManager = ConnectionManager(applicationContext)
        profileManager = ProfileManager()
        rgbController = RgbController()
    }
}
