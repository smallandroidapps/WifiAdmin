package com.location.wifiadmin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.location.wifiadmin.data.Device
import com.location.wifiadmin.data.DeviceStatus
import com.location.wifiadmin.data.DeviceType

@Composable
fun DeviceCard(
    device: Device,
    onPauseToggle: (Boolean) -> Unit,
    onBlockClick: () -> Unit,
    onSpeedLimitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Icon, Device Name, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getDeviceIcon(device.deviceType),
                            contentDescription = device.deviceType.name,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${device.vendor} • ${device.connectionType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Status Badge
                StatusChip(status = device.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details: IP, MAC, Bandwidth limits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "IP: ${device.ipAddress}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "MAC: ${device.macAddress}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                if (!device.isUnlimited) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DL: ${device.downloadLimitMbpsText}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pause / Resume Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (device.isPaused) "Paused" else "Active",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (device.isPaused) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = !device.isPaused && !device.isBlocked,
                        onCheckedChange = { isChecked ->
                            onPauseToggle(!isChecked)
                        },
                        enabled = !device.isBlocked
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Speed Limit Button
                    OutlinedButton(
                        onClick = onSpeedLimitClick,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Speed Limit",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Speed", fontSize = 12.sp)
                    }

                    // Block Button
                    Button(
                        onClick = onBlockClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (device.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.errorContainer,
                            contentColor = if (device.isBlocked) Color.White else MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = if (device.isBlocked) Icons.Default.Lock else Icons.Default.Block,
                            contentDescription = "Block Device",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (device.isBlocked) "Blocked" else "Block", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: DeviceStatus) {
    val (bgColor, textColor, text) = when (status) {
        DeviceStatus.ACTIVE -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Active")
        DeviceStatus.PAUSED -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Paused")
        DeviceStatus.BLOCKED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Blocked")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getDeviceIcon(deviceType: DeviceType): ImageVector {
    return when (deviceType) {
        DeviceType.PHONE -> Icons.Default.Smartphone
        DeviceType.LAPTOP -> Icons.Default.Computer
        DeviceType.SMART_TV -> Icons.Default.Tv
        DeviceType.CONSOLE -> Icons.Default.Gamepad
        DeviceType.IOT -> Icons.Default.Sensors
        DeviceType.TABLET -> Icons.Default.Tablet
        DeviceType.UNKNOWN -> Icons.Default.Devices
    }
}
