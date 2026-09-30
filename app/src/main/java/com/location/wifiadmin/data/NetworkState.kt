package com.location.wifiadmin.data

data class NetworkState(
    val ispName: String = "ACT Fibernet",
    val gatewayIp: String = "192.168.1.1",
    val routerModel: String = "ACT SmartDual Router G-2425",
    val publicIp: String = "106.51.72.189",
    val isConnected: Boolean = true,
    val isScanning: Boolean = false,
    val devices: List<Device> = emptyList(),
    val guestNetwork: GuestNetwork = GuestNetwork(),
    val lastScanTimestamp: Long = System.currentTimeMillis()
) {
    val activeDevicesCount: Int
        get() = devices.count { it.status == DeviceStatus.ACTIVE }

    val pausedDevicesCount: Int
        get() = devices.count { it.status == DeviceStatus.PAUSED }

    val blockedDevicesCount: Int
        get() = devices.count { it.status == DeviceStatus.BLOCKED }
}
