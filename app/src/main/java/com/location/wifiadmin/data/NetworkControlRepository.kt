package com.location.wifiadmin.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NetworkControlRepository(
    private val scanner: NetworkScanner = NetworkScanner()
) {
    private val _networkState = MutableStateFlow(NetworkState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    suspend fun performNetworkScan() {
        _networkState.update { it.copy(isScanning = true) }
        val freshDevices = scanner.scanLocalNetwork()
        _networkState.update { state ->
            val updatedList = freshDevices.map { newDev ->
                // Preserve user-applied status (paused/blocked/limits/priority) if device existed previously
                val existing = state.devices.find { it.macAddress.equals(newDev.macAddress, ignoreCase = true) }
                if (existing != null) {
                    newDev.copy(
                        status = existing.status,
                        downloadLimitKbps = existing.downloadLimitKbps,
                        uploadLimitKbps = existing.uploadLimitKbps,
                        isPriority = existing.isPriority,
                        blockedUntilTimestamp = existing.blockedUntilTimestamp
                    )
                } else {
                    newDev
                }
            }
            state.copy(
                devices = updatedList,
                isScanning = false,
                lastScanTimestamp = System.currentTimeMillis()
            )
        }
    }

    fun togglePriorityBoost(macAddress: String) {
        _networkState.update { state ->
            val updatedDevices = state.devices.map { dev ->
                if (dev.macAddress.equals(macAddress, ignoreCase = true)) {
                    dev.copy(isPriority = !dev.isPriority)
                } else dev
            }
            state.copy(devices = updatedDevices)
        }
    }

    fun pauseDevice(macAddress: String) {
        _networkState.update { state ->
            val updatedDevices = state.devices.map { dev ->
                if (dev.macAddress.equals(macAddress, ignoreCase = true)) {
                    dev.copy(status = DeviceStatus.PAUSED)
                } else dev
            }
            state.copy(devices = updatedDevices)
        }
    }

    fun resumeDevice(macAddress: String) {
        _networkState.update { state ->
            val updatedDevices = state.devices.map { dev ->
                if (dev.macAddress.equals(macAddress, ignoreCase = true)) {
                    dev.copy(status = DeviceStatus.ACTIVE, blockedUntilTimestamp = 0L)
                } else dev
            }
            state.copy(devices = updatedDevices)
        }
    }

    fun blockDevice(macAddress: String, durationMinutes: Int) {
        val untilTimestamp = if (durationMinutes > 0) {
            System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        } else {
            -1L // Indefinite block
        }

        _networkState.update { state ->
            val updatedDevices = state.devices.map { dev ->
                if (dev.macAddress.equals(macAddress, ignoreCase = true)) {
                    dev.copy(
                        status = DeviceStatus.BLOCKED,
                        blockedUntilTimestamp = untilTimestamp
                    )
                } else dev
            }
            state.copy(devices = updatedDevices)
        }
    }

    fun setBandwidthLimit(macAddress: String, maxDownloadKbps: Int, maxUploadKbps: Int) {
        _networkState.update { state ->
            val updatedDevices = state.devices.map { dev ->
                if (dev.macAddress.equals(macAddress, ignoreCase = true)) {
                    dev.copy(
                        downloadLimitKbps = maxDownloadKbps,
                        uploadLimitKbps = maxUploadKbps
                    )
                } else dev
            }
            state.copy(devices = updatedDevices)
        }
    }

    fun createGuestNetwork(ssid: String, durationHours: Int, password: String = "ACTGuest123"): GuestNetwork {
        val expiresAt = System.currentTimeMillis() + (durationHours * 3600 * 1000L)
        val guest = GuestNetwork(
            ssid = ssid.ifBlank { "ACT_Fibernet_Guest" },
            password = password,
            isActive = true,
            durationHours = durationHours,
            expirationTimestamp = expiresAt
        )
        _networkState.update { state ->
            state.copy(guestNetwork = guest)
        }
        return guest
    }

    fun disableGuestNetwork() {
        _networkState.update { state ->
            state.copy(guestNetwork = GuestNetwork())
        }
    }

    fun pauseAllDevices() {
        _networkState.update { state ->
            val updated = state.devices.map { it.copy(status = DeviceStatus.PAUSED) }
            state.copy(devices = updated)
        }
    }

    fun resumeAllDevices() {
        _networkState.update { state ->
            val updated = state.devices.map { it.copy(status = DeviceStatus.ACTIVE) }
            state.copy(devices = updated)
        }
    }

    fun getConnectedDevices(): List<Device> {
        return _networkState.value.devices
    }
}
