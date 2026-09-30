package com.location.wifiadmin.data

enum class DeviceStatus {
    ACTIVE,
    PAUSED,
    BLOCKED
}

enum class DeviceType {
    PHONE,
    LAPTOP,
    SMART_TV,
    CONSOLE,
    IOT,
    TABLET,
    UNKNOWN
}

data class Device(
    val macAddress: String,
    val ipAddress: String,
    val name: String,
    val deviceType: DeviceType,
    val status: DeviceStatus = DeviceStatus.ACTIVE,
    val downloadLimitKbps: Int = 0,
    val uploadLimitKbps: Int = 0,
    val signalStrengthDbm: Int = -55,
    val connectionType: String = "5GHz",
    val blockedUntilTimestamp: Long = 0L,
    val vendor: String = "Generic"
) {
    val isBlocked: Boolean get() = status == DeviceStatus.BLOCKED
    val isPaused: Boolean get() = status == DeviceStatus.PAUSED
    val isUnlimited: Boolean get() = downloadLimitKbps <= 0 && uploadLimitKbps <= 0

    val downloadLimitMbpsText: String
        get() = if (downloadLimitKbps <= 0) "Unlimited" else "${downloadLimitKbps / 1000} Mbps"

    val uploadLimitMbpsText: String
        get() = if (uploadLimitKbps <= 0) "Unlimited" else "${uploadLimitKbps / 1000} Mbps"
}
