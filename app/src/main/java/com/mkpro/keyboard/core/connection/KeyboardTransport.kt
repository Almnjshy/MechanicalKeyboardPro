package com.mkpro.keyboard.core.connection

import kotlinx.coroutines.flow.StateFlow

/**
 * Every transport (Bluetooth HID, USB HID, Wi-Fi/LAN, WebSocket, TCP, UDP...)
 * implements this contract. The UI and the ConnectionManager never talk to a
 * concrete transport directly, so new protocols can be added without touching
 * any screen (per spec: "communication layer must be abstract").
 */
interface KeyboardTransport {
    val type: ConnectionType
    val state: StateFlow<ConnectionState>

    suspend fun scan(): List<DiscoveredDevice>
    suspend fun connect(device: DiscoveredDevice): Boolean
    suspend fun disconnect()
    suspend fun sendKeyEvent(hidReport: ByteArray)
}
