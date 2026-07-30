package com.mkpro.keyboard.core.connection

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Wi-Fi/LAN transport scaffold (TCP/WebSocket to a companion desktop app).
 * Same contract as BluetoothHidTransport - see KeyboardTransport.
 */
class WifiTransport(private val context: Context) : KeyboardTransport {

    override val type: ConnectionType = ConnectionType.WIFI

    private val _state = MutableStateFlow(ConnectionState(type = type))
    override val state: StateFlow<ConnectionState> = _state

    override suspend fun scan(): List<DiscoveredDevice> {
        // TODO: mDNS/NSD discovery of the desktop companion service on the LAN.
        return emptyList()
    }

    override suspend fun connect(device: DiscoveredDevice): Boolean {
        // TODO: open TCP/WebSocket connection to device, handshake, start heartbeat.
        _state.value = _state.value.copy(isConnected = false, deviceName = device.name)
        return false
    }

    override suspend fun disconnect() {
        _state.value = _state.value.copy(isConnected = false)
    }

    override suspend fun sendKeyEvent(hidReport: ByteArray) {
        // TODO: write serialized key event frame to the socket.
    }
}
