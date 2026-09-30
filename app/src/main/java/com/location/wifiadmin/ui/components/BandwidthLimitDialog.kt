package com.location.wifiadmin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.location.wifiadmin.data.Device

@Composable
fun BandwidthLimitDialog(
    device: Device,
    onDismiss: () -> Unit,
    onConfirmLimit: (downloadKbps: Int, uploadKbps: Int) -> Unit
) {
    var downloadMbps by remember { mutableStateOf(if (device.downloadLimitKbps > 0) (device.downloadLimitKbps / 1000).toString() else "10") }
    var uploadMbps by remember { mutableStateOf(if (device.uploadLimitKbps > 0) (device.uploadLimitKbps / 1000).toString() else "2") }
    var isUnlimited by remember { mutableStateOf(device.isUnlimited) }

    val presetSpeedList = listOf(
        1 to "1 Mbps",
        5 to "5 Mbps",
        10 to "10 Mbps",
        50 to "50 Mbps"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Bandwidth Limit: ${device.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Configure max download and upload speed for this device on your ACT Fibernet network.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isUnlimited,
                        onCheckedChange = { isUnlimited = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No Speed Limit (Unlimited)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!isUnlimited) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetSpeedList.forEach { (mbps, label) ->
                            FilterChip(
                                selected = (downloadMbps == mbps.toString()),
                                onClick = {
                                    downloadMbps = mbps.toString()
                                    uploadMbps = (mbps / 2).coerceAtLeast(1).toString()
                                },
                                label = { Text(label) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = downloadMbps,
                        onValueChange = { downloadMbps = it },
                        label = { Text("Max Download Speed (Mbps)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uploadMbps,
                        onValueChange = { uploadMbps = it },
                        label = { Text("Max Upload Speed (Mbps)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isUnlimited) {
                        onConfirmLimit(0, 0)
                    } else {
                        val dl = (downloadMbps.toIntOrNull() ?: 10) * 1000
                        val ul = (uploadMbps.toIntOrNull() ?: 2) * 1000
                        onConfirmLimit(dl, ul)
                    }
                }
            ) {
                Text("Apply Limit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
