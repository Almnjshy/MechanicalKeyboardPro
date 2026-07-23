package com.mkpro.keyboard.ui.screens.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.MkProApplication
import com.mkpro.keyboard.core.connection.ConnectionType
import com.mkpro.keyboard.core.connection.DiscoveredDevice
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary
import kotlinx.coroutines.launch

/**
 * Lists bonded devices reachable over Bluetooth HID and connects on tap.
 * USB/Wi-Fi sections are placeholders until UsbHidTransport / discovery UI
 * for WifiTransport are built - selecting them here is a no-op for now.
 */
@Composable
fun ConnectionScreen(onDeviceConnected: () -> Unit) {
    val context = LocalContext.current
    val connectionManager = remember {
        (context.applicationContext as MkProApplication).connectionManager
    }
    val scope = rememberCoroutineScope()
    val connectionState by connectionManager.connectionState.collectAsState()

    var bondedDevices by remember { mutableStateOf<List<DiscoveredDevice>>(emptyList()) }
    var isScanning by remember { mutableStateOf(false) }
    var connectingDeviceId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "اتصال",
            color = MkTextPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "اختر جهاز مقترن مسبقًا عبر Bluetooth",
            color = MkTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {
                isScanning = true
                scope.launch {
                    bondedDevices = connectionManager.scan(ConnectionType.BLUETOOTH_HID)
                    isScanning = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isScanning) "جارِ البحث..." else "بحث عن الأجهزة المقترنة")
        }

        if (bondedDevices.isEmpty() && !isScanning) {
            Text(
                text = "لا توجد أجهزة مقترنة بعد. قرن الهاتف بالحاسوب من إعدادات Bluetooth أولاً.",
                color = MkTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        bondedDevices.forEach { device ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MkSurface, RoundedCornerShape(14.dp))
                    .clickable {
                        connectingDeviceId = device.id
                        scope.launch {
                            val success = connectionManager.connect(ConnectionType.BLUETOOTH_HID, device)
                            connectingDeviceId = null
                            if (success) onDeviceConnected()
                        }
                    }
                    .padding(16.dp)
            ) {
                Text(text = device.name, color = MkTextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (connectingDeviceId == device.id) "جارِ الاتصال..." else "اضغط للاتصال",
                    color = MkTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (connectionState.isConnected) {
            Button(onClick = onDeviceConnected, modifier = Modifier.fillMaxWidth()) {
                Text("متابعة إلى الكيبورد")
            }
        }
    }
}
