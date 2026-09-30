package com.location.wifiadmin.data

import kotlinx.coroutines.delay

class NetworkScanner {

    suspend fun scanLocalNetwork(): List<Device> {
        // Simulate network ARP probe / Ping scan delay
        delay(1200)

        return listOf(
            Device(
                macAddress = "F4:D4:88:5A:12:34",
                ipAddress = "192.168.1.101",
                name = "John's iPhone 15",
                deviceType = DeviceType.PHONE,
                status = DeviceStatus.ACTIVE,
                downloadLimitKbps = 0,
                uploadLimitKbps = 0,
                signalStrengthDbm = -42,
                connectionType = "5GHz",
                vendor = "Apple"
            ),
            Device(
                macAddress = "3C:22:FB:89:BC:11",
                ipAddress = "192.168.1.105",
                name = "Living Room LG OLED TV",
                deviceType = DeviceType.SMART_TV,
                status = DeviceStatus.ACTIVE,
                downloadLimitKbps = 25000,
                uploadLimitKbps = 5000,
                signalStrengthDbm = -58,
                connectionType = "5GHz",
                vendor = "LG Electronics"
            ),
            Device(
                macAddress = "70:85:C2:E1:90:77",
                ipAddress = "192.168.1.112",
                name = "MacBook Pro M3",
                deviceType = DeviceType.LAPTOP,
                status = DeviceStatus.ACTIVE,
                downloadLimitKbps = 0,
                uploadLimitKbps = 0,
                signalStrengthDbm = -38,
                connectionType = "5GHz",
                vendor = "Apple"
            ),
            Device(
                macAddress = "E4:5F:01:2C:44:89",
                ipAddress = "192.168.1.120",
                name = "PlayStation 5",
                deviceType = DeviceType.CONSOLE,
                status = DeviceStatus.ACTIVE,
                downloadLimitKbps = 0,
                uploadLimitKbps = 0,
                signalStrengthDbm = -65,
                connectionType = "Ethernet",
                vendor = "Sony"
            ),
            Device(
                macAddress = "80:7D:3A:99:12:00",
                ipAddress = "192.168.1.135",
                name = "Samsung Galaxy S24",
                deviceType = DeviceType.PHONE,
                status = DeviceStatus.ACTIVE,
                downloadLimitKbps = 10000,
                uploadLimitKbps = 2000,
                signalStrengthDbm = -50,
                connectionType = "5GHz",
                vendor = "Samsung"
            ),
            Device(
                macAddress = "60:01:94:44:0B:EE",
                ipAddress = "192.168.1.140",
                name = "Smart Plug - Balcony",
                deviceType = DeviceType.IOT,
                status = DeviceStatus.ACTIVE,
                downloadLimitKbps = 1000,
                uploadLimitKbps = 500,
                signalStrengthDbm = -72,
                connectionType = "2.4GHz",
                vendor = "Tuya"
            ),
            Device(
                macAddress = "AC:D1:B8:33:45:90",
                ipAddress = "192.168.1.150",
                name = "iPad Air 5th Gen",
                deviceType = DeviceType.TABLET,
                status = DeviceStatus.PAUSED,
                downloadLimitKbps = 0,
                uploadLimitKbps = 0,
                signalStrengthDbm = -54,
                connectionType = "5GHz",
                vendor = "Apple"
            )
        )
    }
}
