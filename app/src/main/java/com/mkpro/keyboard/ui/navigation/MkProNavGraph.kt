package com.mkpro.keyboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mkpro.keyboard.ui.screens.connection.ConnectionScreen
import com.mkpro.keyboard.ui.screens.home.HomeScreen
import com.mkpro.keyboard.ui.screens.keyboard.KeyboardScreen
import com.mkpro.keyboard.ui.screens.splash.SplashScreen

/**
 * Splash -> Home (enable the IME - the real product) -> optional PC
 * connection mode (Connection -> Keyboard, a preview/simulator of the
 * secondary desktop-keyboard feature; the actual PC typing happens from
 * KeyboardService whenever "PC Keys" layer + Bluetooth are both active).
 */
@Composable
fun MkProNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(onOpenPcConnectionMode = { navController.navigate(Screen.Connection.route) })
        }

        composable(Screen.Connection.route) {
            ConnectionScreen(onDeviceConnected = { navController.navigate(Screen.Keyboard.route) })
        }

        composable(Screen.Keyboard.route) {
            KeyboardScreen()
        }
    }
}
