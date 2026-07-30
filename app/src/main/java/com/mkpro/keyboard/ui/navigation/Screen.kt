package com.mkpro.keyboard.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Connection : Screen("connection")
    data object Keyboard : Screen("keyboard")
    data object Settings : Screen("settings")
}
