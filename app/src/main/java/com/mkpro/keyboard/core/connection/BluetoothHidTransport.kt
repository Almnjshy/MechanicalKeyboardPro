package com.mkpro.keyboard.core.connection

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.mkpro.keyboard.core.connection.hid.HidKeyboardDescriptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val SDP_NAME = "Mechanical Keyboard Pro"
private const val SDP_DESCRIPTION = "Phone as a premium mechanical keyboard"
private const val SDP_PROVIDER = "MKPro"

/**
 * Bluetooth HID transport backed by the real android.bluetooth.BluetoothHidDevice
 * profile: the phone registers itself as an HID peripheral (like a physical
 * BT keyboard) and the desktop OS pairs with it directly - no companion app
 * needed on the PC side.
 *
 * Requires API 28+ (BluetoothHidDevice profile) and BLUETOOTH_CONNECT at
 * runtime on API 31+. On older devices connect()/scan() safely no-op and
 * report failure so ConnectionManager can fall back to another transport.
 */
class BluetoothHidTransport(private val context: Context) : KeyboardTransport {

    override val type: ConnectionType = ConnectionType.BLUETOOTH_HID

    private val _state = MutableStateFlow(ConnectionState(type = type))
    override val state: StateFlow<ConnectionState> = _state

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    }

    private var hidDevice: BluetoothHidDevice? = null
    private var connectedDevice: BluetoothDevice? = null

    private fun hasConnectPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun scan(): List<DiscoveredDevice> {
        val adapter = bluetoothAdapter ?: return emptyList()
        if (!hasConnectPermission()) return emptyList()

        // Bonded devices are the practical starting point for HID pairing;
        // a full discovery flow (startDiscovery + BroadcastReceiver) can be
        // layered on top once the UI has a device-picker + permission prompt.
        return runCatching {
            adapter.bondedDevices.map { device ->
                DiscoveredDevice(id = device.address, name = device.name ?: device.address, type = type)
            }
        }.getOrDefault(emptyList())
    }

    override suspend fun connect(device: DiscoveredDevice): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        val adapter = bluetoothAdapter ?: return false
        if (!hasConnectPermission()) return false

        val proxy = registerHidProfile(adapter) ?: return false
        hidDevice = proxy

        val btDevice = adapter.bondedDevices.firstOrNull { it.address == device.id } ?: return false

        val connected = runCatching { proxy.connect(btDevice) }.getOrDefault(false)
        if (connected) {
            connectedDevice = btDevice
            _state.value = _state.value.copy(isConnected = true, deviceName = btDevice.name)
        }
        return connected
    }

    override suspend fun disconnect() {
        val proxy = hidDevice
        val device = connectedDevice
        if (proxy != null && device != null && hasConnectPermission()) {
            runCatching { proxy.disconnect(device) }
        }
        connectedDevice = null
        _state.value = _state.value.copy(isConnected = false)
    }

    /**
     * Sends a pre-built 8-byte HID report as-is. Callers are responsible for
     * following a keypress report with HidReportBuilder.buildKeyUp() - kept
     * explicit here (rather than auto-appended) so callers that already send
     * their own release report, like MacroEngine, don't double-send.
     */
    override suspend fun sendKeyEvent(hidReport: ByteArray) {
        val proxy = hidDevice ?: return
        val device = connectedDevice ?: return
        if (!hasConnectPermission()) return

        runCatching {
            proxy.sendReport(device, HidKeyboardDescriptor.REPORT_ID.toInt(), hidReport)
        }
    }

    /**
     * Registers this app as an HID Device profile peripheral. Suspends until
     * BluetoothProfile.ServiceListener resolves, since getProfileProxy() is
     * callback-based rather than suspend-friendly.
     */
    private suspend fun registerHidProfile(adapter: BluetoothAdapter): BluetoothHidDevice? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    val hid = proxy as BluetoothHidDevice
                    val sdpSettings = BluetoothHidDeviceAppSdpSettings(
                        SDP_NAME,
                        SDP_DESCRIPTION,
                        SDP_PROVIDER,
                        BluetoothHidDevice.SUBCLASS1_COMBO,
                        HidKeyboardDescriptor.REPORT_DESCRIPTOR
                    )
                    val qos = BluetoothHidDeviceAppQosSettings(
                        BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED,
                        800, 9, 0, 11250, 11250
                    )
                    val callback = object : BluetoothHidDevice.Callback() {
                        override fun onConnectionStateChanged(device: BluetoothDevice?, hidState: Int) {
                            val isConnected = hidState == BluetoothProfile.STATE_CONNECTED
                            _state.value = _state.value.copy(isConnected = isConnected, deviceName = device?.name)
                            if (!isConnected && device == connectedDevice) connectedDevice = null
                        }
                    }
                    val registered = runCatching {
                        hid.registerApp(sdpSettings, qos, qos, context.mainExecutor, callback)
                    }.getOrDefault(false)

                    if (continuation.isActive) continuation.resume(if (registered) hid else null)
                }

                override fun onServiceDisconnected(profile: Int) {
                    hidDevice = null
                }
            }

            val requested = adapter.getProfileProxy(context, listener, BluetoothProfile.HID_DEVICE)
            if (!requested && continuation.isActive) continuation.resume(null)

            continuation.invokeOnCancellation { /* profile proxy is released in disconnect()/onCleared */ }
        }
}
