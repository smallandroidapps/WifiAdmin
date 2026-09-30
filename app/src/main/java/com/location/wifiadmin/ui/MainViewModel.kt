package com.location.wifiadmin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.location.wifiadmin.data.Device
import com.location.wifiadmin.data.DeviceStatus
import com.location.wifiadmin.data.NetworkControlRepository
import com.location.wifiadmin.data.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class DeviceFilter {
    ALL,
    ACTIVE,
    PAUSED,
    BLOCKED
}

class MainViewModel(
    private val repository: NetworkControlRepository = NetworkControlRepository()
) : ViewModel() {

    val networkState: StateFlow<NetworkState> = repository.networkState

    private val _activeFilter = MutableStateFlow(DeviceFilter.ALL)
    val activeFilter: StateFlow<DeviceFilter> = _activeFilter.asStateFlow()

    private val _selectedDeviceForBlock = MutableStateFlow<Device?>(null)
    val selectedDeviceForBlock: StateFlow<Device?> = _selectedDeviceForBlock.asStateFlow()

    private val _selectedDeviceForSpeedLimit = MutableStateFlow<Device?>(null)
    val selectedDeviceForSpeedLimit: StateFlow<Device?> = _selectedDeviceForSpeedLimit.asStateFlow()

    private val _showGuestNetworkDialog = MutableStateFlow(false)
    val showGuestNetworkDialog: StateFlow<Boolean> = _showGuestNetworkDialog.asStateFlow()

    init {
        // Automatically perform initial scan on start
        triggerScan()
    }

    fun triggerScan() {
        viewModelScope.launch {
            repository.performNetworkScan()
        }
    }

    fun setFilter(filter: DeviceFilter) {
        if (_activeFilter.value == filter) {
            _activeFilter.value = DeviceFilter.ALL
        } else {
            _activeFilter.value = filter
        }
    }

    fun clearFilter() {
        _activeFilter.value = DeviceFilter.ALL
    }

    fun pauseDevice(macAddress: String) {
        repository.pauseDevice(macAddress)
    }

    fun resumeDevice(macAddress: String) {
        repository.resumeDevice(macAddress)
    }

    fun openBlockDialog(device: Device) {
        _selectedDeviceForBlock.value = device
    }

    fun closeBlockDialog() {
        _selectedDeviceForBlock.value = null
    }

    fun blockDevice(macAddress: String, durationMinutes: Int) {
        repository.blockDevice(macAddress, durationMinutes)
        closeBlockDialog()
    }

    fun openSpeedLimitDialog(device: Device) {
        _selectedDeviceForSpeedLimit.value = device
    }

    fun closeSpeedLimitDialog() {
        _selectedDeviceForSpeedLimit.value = null
    }

    fun setBandwidthLimit(macAddress: String, downloadKbps: Int, uploadKbps: Int) {
        repository.setBandwidthLimit(macAddress, downloadKbps, uploadKbps)
        closeSpeedLimitDialog()
    }

    fun openGuestNetworkDialog() {
        _showGuestNetworkDialog.value = true
    }

    fun closeGuestNetworkDialog() {
        _showGuestNetworkDialog.value = false
    }

    fun createGuestNetwork(ssid: String, durationHours: Int, password: String) {
        repository.createGuestNetwork(ssid, durationHours, password)
        closeGuestNetworkDialog()
    }

    fun disableGuestNetwork() {
        repository.disableGuestNetwork()
    }

    fun pauseAll() {
        repository.pauseAllDevices()
    }

    fun resumeAll() {
        repository.resumeAllDevices()
    }
}
