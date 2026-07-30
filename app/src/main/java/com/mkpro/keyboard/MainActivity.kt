package com.mkpro.keyboard

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.mkpro.keyboard.core.settings.AppSettings
import com.mkpro.keyboard.ui.navigation.Screen
import com.mkpro.keyboard.ui.navigation.MkProNavGraph
import com.mkpro.keyboard.ui.theme.MechanicalKeyboardProTheme

/** Extra key used by KeyboardService to deep-link straight into a screen (e.g. Settings) instead of the Splash/Home flow. */
const val EXTRA_OPEN_ROUTE = "open_route"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val requestedRoute = intent?.getStringExtra(EXTRA_OPEN_ROUTE)
        setContent {
            MkProApp(startDestination = requestedRoute ?: Screen.Splash.route)
        }
    }
}

/** Bluetooth runtime permissions only exist from API 31 (S) onward. */
private fun bluetoothPermissions(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
    } else {
        emptyArray()
    }

/** POST_NOTIFICATIONS only exists from API 33 (Tiramisu) onward - needed so CrashReporter's diagnostic notification can actually show. */
private fun notificationPermission(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

@Composable
private fun MkProApp(startDestination: String) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* ConnectionScreen re-checks state on its own when the user taps a transport */ }

    LaunchedEffect(Unit) {
        val needed = bluetoothPermissions() + notificationPermission()
        if (needed.isNotEmpty()) permissionLauncher.launch(needed)
    }

    val settingsRepository = (context.applicationContext as MkProApplication).settingsRepository
    val settings by settingsRepository.settingsFlow.collectAsState(initial = AppSettings())

    MechanicalKeyboardProTheme(themeVariant = settings.theme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            MkProNavGraph(navController = navController, startDestination = startDestination)
        }
    }
}
