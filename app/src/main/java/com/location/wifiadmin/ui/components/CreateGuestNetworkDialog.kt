package com.location.wifiadmin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CreateGuestNetworkDialog(
    onDismiss: () -> Unit,
    onConfirmCreate: (ssid: String, durationHours: Int, password: String) -> Unit
) {
    var ssid by remember { mutableStateOf("ACT_Fibernet_Guest") }
    var password by remember { mutableStateOf("Guest@1234") }
    var selectedHours by remember { mutableStateOf(4) }

    val hourOptions = listOf(1, 2, 4, 8, 24)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Guest Wi-Fi Network",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Broadcast an isolated guest Wi-Fi SSID for visitors on your ACT Fibernet router.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = ssid,
                    onValueChange = { ssid = it },
                    label = { Text("Guest Wi-Fi Name (SSID)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Guest Wi-Fi Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Active Duration (Hours):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    hourOptions.forEach { hrs ->
                        FilterChip(
                            selected = (selectedHours == hrs),
                            onClick = { selectedHours = hrs },
                            label = { Text("${hrs}h") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmCreate(
                        ssid.ifBlank { "ACT_Fibernet_Guest" },
                        selectedHours,
                        password.ifBlank { "Guest1234" }
                    )
                }
            ) {
                Text("Enable Guest Wi-Fi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
