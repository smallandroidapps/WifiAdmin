package com.location.wifiadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.location.wifiadmin.data.DeviceStatus
import com.location.wifiadmin.ui.DeviceFilter
import com.location.wifiadmin.ui.MainViewModel
import com.location.wifiadmin.ui.components.*
import com.location.wifiadmin.ui.theme.WifiAdminTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiAdminTheme {
                val networkState by viewModel.networkState.collectAsState()
                val activeFilter by viewModel.activeFilter.collectAsState()
                val selectedForBlock by viewModel.selectedDeviceForBlock.collectAsState()
                val selectedForSpeedLimit by viewModel.selectedDeviceForSpeedLimit.collectAsState()
                val showGuestDialog by viewModel.showGuestNetworkDialog.collectAsState()
                val showQrDialog by viewModel.showQrShareDialog.collectAsState()

                val displayedDevices = remember(networkState.devices, activeFilter) {
                    when (activeFilter) {
                        DeviceFilter.ALL -> networkState.devices
                        DeviceFilter.ACTIVE -> networkState.devices.filter { it.status == DeviceStatus.ACTIVE }
                        DeviceFilter.PAUSED -> networkState.devices.filter { it.status == DeviceStatus.PAUSED }
                        DeviceFilter.BLOCKED -> networkState.devices.filter { it.status == DeviceStatus.BLOCKED }
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Wifi,
                                        contentDescription = "Wi-Fi Icon",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ACT Fibernet Manager",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        PullToRefreshBox(
                            isRefreshing = networkState.isScanning,
                            onRefresh = { viewModel.triggerScan() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                // Router Header Card with filter badges
                                item {
                                    NetworkHeaderCard(
                                        networkState = networkState,
                                        selectedFilter = activeFilter,
                                        onFilterSelected = { filter ->
                                            viewModel.setFilter(filter)
                                        }
                                    )
                                }

                                // Quick Controls Bar
                                item {
                                    QuickActionBar(
                                        isScanning = networkState.isScanning,
                                        onScanClick = { viewModel.triggerScan() },
                                        onGuestNetworkClick = { viewModel.openGuestNetworkDialog() },
                                        onShareQrClick = { viewModel.openQrShareDialog() },
                                        onPauseAllClick = { viewModel.pauseAll() },
                                        onResumeAllClick = { viewModel.resumeAll() }
                                    )
                                }

                                // Devices Section Title & Filter Chip Bar
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Connected Devices (${displayedDevices.size})",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )

                                            if (networkState.isScanning) {
                                                Text(
                                                    text = "Scanning...",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }

                                        if (activeFilter != DeviceFilter.ALL) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                FilterChip(
                                                    selected = true,
                                                    onClick = { viewModel.clearFilter() },
                                                    label = { Text("Filter: ${activeFilter.name}") },
                                                    trailingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Clear Filter",
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                TextButton(onClick = { viewModel.clearFilter() }) {
                                                    Text("Show All")
                                                }
                                            }
                                        }
                                    }
                                }

                                // Connected Device Cards
                                items(
                                    items = displayedDevices,
                                    key = { it.macAddress }
                                ) { device ->
                                    DeviceCard(
                                        device = device,
                                        onPauseToggle = { shouldPause ->
                                            if (shouldPause) {
                                                viewModel.pauseDevice(device.macAddress)
                                            } else {
                                                viewModel.resumeDevice(device.macAddress)
                                            }
                                        },
                                        onPriorityToggle = {
                                            viewModel.togglePriorityBoost(device.macAddress)
                                        },
                                        onBlockClick = { viewModel.openBlockDialog(device) },
                                        onSpeedLimitClick = { viewModel.openSpeedLimitDialog(device) }
                                    )
                                }
                            }
                        }

                        // Dialogs
                        selectedForBlock?.let { device ->
                            BlockDeviceDialog(
                                device = device,
                                onDismiss = { viewModel.closeBlockDialog() },
                                onConfirmBlock = { mins ->
                                    viewModel.blockDevice(device.macAddress, mins)
                                },
                                onUnblock = {
                                    viewModel.resumeDevice(device.macAddress)
                                    viewModel.closeBlockDialog()
                                }
                            )
                        }

                        selectedForSpeedLimit?.let { device ->
                            BandwidthLimitDialog(
                                device = device,
                                onDismiss = { viewModel.closeSpeedLimitDialog() },
                                onConfirmLimit = { dl, ul ->
                                    viewModel.setBandwidthLimit(device.macAddress, dl, ul)
                                }
                            )
                        }

                        if (showGuestDialog) {
                            CreateGuestNetworkDialog(
                                onDismiss = { viewModel.closeGuestNetworkDialog() },
                                onConfirmCreate = { ssid, hours, pass ->
                                    viewModel.createGuestNetwork(ssid, hours, pass)
                                }
                            )
                        }

                        if (showQrDialog) {
                            ShareWifiQrDialog(
                                networkState = networkState,
                                onDismiss = { viewModel.closeQrShareDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}
