package com.mkpro.keyboard.core.profiles

data class Profile(
    val id: String,
    val name: String,
    val defaultLayerId: String,
    val rgbEffectId: String? = null,
    val autoSwitchAppPackage: String? = null // e.g. auto-activate when this PC app is focused
)
