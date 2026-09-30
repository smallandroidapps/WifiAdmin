package com.location.wifiadmin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.location.wifiadmin.data.Device

@Composable
fun BlockDeviceDialog(
    device: Device,
    onDismiss: () -> Unit,
    onConfirmBlock: (durationMinutes: Int) -> Unit,
    onUnblock: () -> Unit
) {
    var selectedDurationMins by remember { mutableStateOf(60) }

    val options = listOf(
        30 to "30 Minutes",
        60 to "1 Hour",
        120 to "2 Hours",
        720 to "12 Hours",
        -1 to "Indefinitely"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (device.isBlocked) "Unblock or Change Block" else "Block Device",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Device: ${device.name} (${device.ipAddress})",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select block duration:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                options.forEach { (mins, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedDurationMins == mins),
                            onClick = { selectedDurationMins = mins }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmBlock(selectedDurationMins) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Block Device")
            }
        },
        dismissButton = {
            Row {
                if (device.isBlocked) {
                    TextButton(onClick = onUnblock) {
                        Text("Unblock Now")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
