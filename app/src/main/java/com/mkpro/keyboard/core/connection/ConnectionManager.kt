package com.mkpro.keyboard.core.connection

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single entry point the rest of the app talks to for connectivity.
 * Holds one active KeyboardTransport at a time and exposes its state.
 * Adding a new protocol = implement KeyboardTransport + register it here;
 * nothing else in the app changes.
 */
class ConnectionManager(private val context: Context) {

    private val transports: Map<ConnectionType, KeyboardTransport> = mapOf(
        ConnectionType.BLUETOOTH_HID to BluetoothHidTransport(context),
        ConnectionType.WIFI to WifiTransport(context)
        // ConnectionType.USB_HID to UsbHidTransport(context) - add when implemented
    )

    private var activeTransport: KeyboardTransport? = null

    private val _connectionState = MutableStateFlow(ConnectionState())
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    suspend fun scan(type: ConnectionType): List<DiscoveredDevice> {
        return transports[type]?.scan().orEmpty()
    }

    suspend fun connect(type: ConnectionType, device: DiscoveredDevice): Boolean {
        val transport = transports[type] ?: return false
        val success = transport.connect(device)
        if (success) {
            activeTransport = transport
            _connectionState.value = transport.state.value
        }
        return success
    }

    suspend fun disconnect() {
        activeTransport?.disconnect()
        activeTransport = null
        _connectionState.value = ConnectionState()
    }

    suspend fun sendKeyEvent(hidReport: ByteArray) {
        activeTransport?.sendKeyEvent(hidReport)
    }
}
