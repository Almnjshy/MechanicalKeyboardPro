package com.mkpro.keyboard.core.connection

enum class ConnectionType {
    BLUETOOTH_HID,
    USB_HID,
    WIFI,
    NONE
}

data class ConnectionState(
    val type: ConnectionType = ConnectionType.NONE,
    val isConnected: Boolean = false,
    val deviceName: String? = null,
    val signalStrength: Int = 0, // 0-100
    val latencyMs: Int = 0,
    val batteryPercent: Int = 100
)

data class DiscoveredDevice(
    val id: String,
    val name: String,
    val type: ConnectionType
)
