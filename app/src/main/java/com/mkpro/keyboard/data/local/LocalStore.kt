package com.mkpro.keyboard.data.local

import android.content.Context

/**
 * Placeholder for on-device persistence of macros, profiles and custom
 * layouts (Room DB in a later pass). Kept behind this class so
 * MacroEngine/ProfileManager can be pointed at real storage without any
 * UI-layer changes - mirrors the same "swap the implementation, not the
 * contract" pattern used in core/connection.
 */
class LocalStore(private val context: Context) {
    // TODO: Room database with MacroDao / ProfileDao / CustomLayoutDao
}
