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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mkpro.keyboard.ui.navigation.MkProNavGraph
import com.mkpro.keyboard.ui.theme.MechanicalKeyboardProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MkProApp()
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

@Composable
private fun MkProApp() {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* ConnectionScreen re-checks state on its own when the user taps a transport */ }

    LaunchedEffect(Unit) {
        val needed = bluetoothPermissions()
        if (needed.isNotEmpty()) permissionLauncher.launch(needed)
    }

    MechanicalKeyboardProTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            MkProNavGraph(navController = navController)
        }
    }
}
