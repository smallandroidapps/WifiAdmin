package com.location.wifiadmin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickActionBar(
    isScanning: Boolean,
    onScanClick: () -> Unit,
    onGuestNetworkClick: () -> Unit,
    onPauseAllClick: () -> Unit,
    onResumeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Quick Control Options",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Scan Button
            Button(
                onClick = onScanClick,
                enabled = !isScanning,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isScanning) "Scanning..." else "Scan", fontSize = 12.sp)
            }

            // Guest Network Button
            OutlinedButton(
                onClick = onGuestNetworkClick,
                modifier = Modifier.weight(1.2f),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WifiTethering,
                    contentDescription = "Guest Wi-Fi",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Guest Wi-Fi", fontSize = 12.sp)
            }

            // Pause All
            OutlinedIconButton(
                onClick = onPauseAllClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PauseCircle,
                    contentDescription = "Pause All Devices",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            // Resume All
            OutlinedIconButton(
                onClick = onResumeAllClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Resume All Devices",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
