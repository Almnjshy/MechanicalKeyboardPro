package com.mkpro.keyboard.core.connection.hid

/** Bitmask values for the modifier byte (byte 0) of the boot keyboard report. */
object HidModifiers {
    const val LEFT_CTRL: Int = 0x01
    const val LEFT_SHIFT: Int = 0x02
    const val LEFT_ALT: Int = 0x04
    const val LEFT_GUI: Int = 0x08 // Windows/Command key
    const val RIGHT_CTRL: Int = 0x10
    const val RIGHT_SHIFT: Int = 0x20
    const val RIGHT_ALT: Int = 0x40
    const val RIGHT_GUI: Int = 0x80
}

/**
 * Builds the 8-byte HID boot-protocol keyboard report:
 * [modifierMask, reserved(0), key1..key6].
 * Supports up to 6 simultaneous non-modifier key presses (standard rollover
 * limit for the boot descriptor - see HidKeyboardDescriptor for the NKRO note).
 */
object HidReportBuilder {

    fun build(modifierMask: Int, usageCodes: List<Int>): ByteArray {
        val report = ByteArray(HidKeyboardDescriptor.REPORT_SIZE_BYTES)
        report[0] = modifierMask.toByte()
        report[1] = 0 // reserved
        usageCodes.take(6).forEachIndexed { index, code ->
            report[2 + index] = code.toByte()
        }
        return report
    }

    /** All-zero report = every key released. Must be sent after each keypress. */
    fun buildKeyUp(): ByteArray = ByteArray(HidKeyboardDescriptor.REPORT_SIZE_BYTES)
}
