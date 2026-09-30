package com.location.wifiadmin

import com.location.wifiadmin.data.DeviceStatus
import com.location.wifiadmin.data.NetworkControlRepository
import com.location.wifiadmin.data.NetworkScanner
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NetworkControlRepositoryTest {

    private lateinit var repository: NetworkControlRepository

    @Before
    fun setUp() = runBlocking {
        repository = NetworkControlRepository(NetworkScanner())
        repository.performNetworkScan()
    }

    @Test
    fun testPerformNetworkScan_populatesDevices() {
        val state = repository.networkState.value
        assertFalse("Devices list should not be empty after scan", state.devices.isEmpty())
        assertTrue("Active devices count should be greater than 0", state.activeDevicesCount > 0)
    }

    @Test
    fun testPauseAndResumeDevice() {
        val targetMac = repository.getConnectedDevices().first().macAddress

        repository.pauseDevice(targetMac)
        var updatedDev = repository.getConnectedDevices().find { it.macAddress == targetMac }
        assertNotNull(updatedDev)
        assertEquals(DeviceStatus.PAUSED, updatedDev?.status)

        repository.resumeDevice(targetMac)
        updatedDev = repository.getConnectedDevices().find { it.macAddress == targetMac }
        assertNotNull(updatedDev)
        assertEquals(DeviceStatus.ACTIVE, updatedDev?.status)
    }

    @Test
    fun testBlockDevice() {
        val targetMac = repository.getConnectedDevices().first().macAddress

        repository.blockDevice(targetMac, durationMinutes = 60)
        val blockedDev = repository.getConnectedDevices().find { it.macAddress == targetMac }
        assertNotNull(blockedDev)
        assertEquals(DeviceStatus.BLOCKED, blockedDev?.status)
        assertTrue("Blocked until timestamp should be set", blockedDev?.blockedUntilTimestamp!! > System.currentTimeMillis())
    }

    @Test
    fun testSetBandwidthLimit() {
        val targetMac = repository.getConnectedDevices().first().macAddress

        repository.setBandwidthLimit(targetMac, maxDownloadKbps = 10000, maxUploadKbps = 2000)
        val limitedDev = repository.getConnectedDevices().find { it.macAddress == targetMac }
        assertNotNull(limitedDev)
        assertEquals(10000, limitedDev?.downloadLimitKbps)
        assertEquals(2000, limitedDev?.uploadLimitKbps)
    }

    @Test
    fun testCreateGuestNetwork() {
        val guest = repository.createGuestNetwork(ssid = "ACT_Home_Guest", durationHours = 4, password = "Pass1234")
        val state = repository.networkState.value

        assertTrue("Guest network should be active", state.guestNetwork.isActive)
        assertEquals("ACT_Home_Guest", state.guestNetwork.ssid)
        assertEquals(4, state.guestNetwork.durationHours)
        assertNotNull(guest)
    }

    @Test
    fun testPauseAllAndResumeAllDevices() {
        repository.pauseAllDevices()
        var state = repository.networkState.value
        assertEquals(0, state.activeDevicesCount)
        assertEquals(state.devices.size, state.pausedDevicesCount)

        repository.resumeAllDevices()
        state = repository.networkState.value
        assertEquals(state.devices.size, state.activeDevicesCount)
        assertEquals(0, state.pausedDevicesCount)
    }
}
