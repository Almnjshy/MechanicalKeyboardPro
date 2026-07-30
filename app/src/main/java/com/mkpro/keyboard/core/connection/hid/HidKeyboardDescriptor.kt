package com.mkpro.keyboard.core.connection.hid

/**
 * Standard 8-byte "boot protocol" keyboard report descriptor:
 * byte0 = modifier bitmask, byte1 = reserved, bytes2-7 = up to 6 simultaneous
 * key usage codes (N-key rollover beyond 6 needs a custom/NKRO descriptor -
 * left as a follow-up once boot-protocol HID is verified working end to end).
 *
 * This is the exact descriptor most PC Bluetooth/USB HID hosts expect out of
 * the box, so it's the safest starting point before adding NKRO or a
 * combined keyboard+consumer-control descriptor.
 */
object HidKeyboardDescriptor {

    val REPORT_DESCRIPTOR: ByteArray = byteArrayOf(
        0x05, 0x01,       // Usage Page (Generic Desktop)
        0x09, 0x06,       // Usage (Keyboard)
        0xA1.toByte(), 0x01, // Collection (Application)
        0x05, 0x07,       //   Usage Page (Key Codes)
        0x19, 0xE0.toByte(), //   Usage Minimum (224)
        0x29, 0xE7.toByte(), //   Usage Maximum (231)
        0x15, 0x00,       //   Logical Minimum (0)
        0x25, 0x01,       //   Logical Maximum (1)
        0x75, 0x01,       //   Report Size (1)
        0x95.toByte(), 0x08, //   Report Count (8) - modifier byte
        0x81.toByte(), 0x02, //   Input (Data, Variable, Absolute) - modifier byte
        0x95.toByte(), 0x01, //   Report Count (1)
        0x75, 0x08,       //   Report Size (8)
        0x81.toByte(), 0x01, //   Input (Constant) - reserved byte
        0x95.toByte(), 0x06, //   Report Count (6)
        0x75, 0x08,       //   Report Size (8)
        0x15, 0x00,       //   Logical Minimum (0)
        0x25, 0x65,       //   Logical Maximum (101)
        0x05, 0x07,       //   Usage Page (Key Codes)
        0x19, 0x00,       //   Usage Minimum (0)
        0x29, 0x65,       //   Usage Maximum (101)
        0x81.toByte(), 0x00, //   Input (Data, Array) - up to 6 keys
        0xC0.toByte()      // End Collection
    )

    const val REPORT_ID: Byte = 0
    const val REPORT_SIZE_BYTES = 8
}
