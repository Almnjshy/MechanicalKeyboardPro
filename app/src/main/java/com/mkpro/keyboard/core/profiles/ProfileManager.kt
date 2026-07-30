package com.mkpro.keyboard.core.profiles

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileManager {

    private val defaultProfiles = listOf(
        Profile(id = "default", name = "Default", defaultLayerId = "default")
    )

    private val _profiles = MutableStateFlow(defaultProfiles)
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _activeProfileId = MutableStateFlow(defaultProfiles.first().id)
    val activeProfileId: StateFlow<String> = _activeProfileId.asStateFlow()

    fun activate(profileId: String) {
        if (_profiles.value.any { it.id == profileId }) {
            _activeProfileId.value = profileId
        }
    }

    fun save(profile: Profile) {
        _profiles.value = _profiles.value.filterNot { it.id == profile.id } + profile
    }

    fun delete(profileId: String) {
        _profiles.value = _profiles.value.filterNot { it.id == profileId }
    }

    /** Called when the PC reports a foreground-app change, if a transport supports it. */
    fun onForegroundAppChanged(packageOrProcessName: String) {
        _profiles.value.firstOrNull { it.autoSwitchAppPackage == packageOrProcessName }
            ?.let { activate(it.id) }
    }
}
