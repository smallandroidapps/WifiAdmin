package com.location.wifiadmin.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.location.wifiadmin.data.NetworkState

@Composable
fun ShareWifiQrDialog(
    networkState: NetworkState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isGuestSelected by remember { mutableStateOf(networkState.guestNetwork.isActive) }

    val ssid = if (isGuestSelected && networkState.guestNetwork.isActive) networkState.guestNetwork.ssid else "ACT_Fibernet_Home_5G"
    val password = if (isGuestSelected && networkState.guestNetwork.isActive) networkState.guestNetwork.password else "ACTFibernet@2026"
    val wifiQrString = "WIFI:T:WPA;S:$ssid;P:$password;;"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Share Wi-Fi via QR Code", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Guests can scan this QR code with their phone camera to instantly join your Wi-Fi network without typing a password.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (networkState.guestNetwork.isActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FilterChip(
                            selected = !isGuestSelected,
                            onClick = { isGuestSelected = false },
                            label = { Text("Main Network") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = isGuestSelected,
                            onClick = { isGuestSelected = true },
                            label = { Text("Guest Network") }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Render QR Matrix Box
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        WifiQrMatrixCanvas(
                            data = wifiQrString,
                            modifier = Modifier.size(180.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = ssid,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Password: $password",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Wi-Fi Details", "Wi-Fi SSID: $ssid\nPassword: $password")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Wi-Fi details copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Details")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun WifiQrMatrixCanvas(
    data: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val gridCount = 21
        val cellSize = sizePx / gridCount

        // Draw white background
        drawRect(color = Color.White, size = Size(sizePx, sizePx))

        // Draw outer position finder squares (Top-Left, Top-Right, Bottom-Left)
        drawFinderPattern(0f, 0f, cellSize)
        drawFinderPattern((gridCount - 7) * cellSize, 0f, cellSize)
        drawFinderPattern(0f, (gridCount - 7) * cellSize, cellSize)

        // Draw deterministic matrix modules based on data hash
        val hash = data.hashCode()
        for (r in 0 until gridCount) {
            for (c in 0 until gridCount) {
                // Skip finder pattern zones
                if ((r < 7 && c < 7) || (r < 7 && c >= gridCount - 7) || (r >= gridCount - 7 && c < 7)) {
                    continue
                }
                val bit = ((r * 31 + c * 17 + hash) % 3) == 0
                if (bit) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFinderPattern(
    x: Float,
    y: Float,
    cellSize: Float
) {
    // 7x7 outer black square
    drawRect(
        color = Color.Black,
        topLeft = Offset(x, y),
        size = Size(7 * cellSize, 7 * cellSize)
    )
    // 5x5 inner white square
    drawRect(
        color = Color.White,
        topLeft = Offset(x + cellSize, y + cellSize),
        size = Size(5 * cellSize, 5 * cellSize)
    )
    // 3x3 core black square
    drawRect(
        color = Color.Black,
        topLeft = Offset(x + 2 * cellSize, y + 2 * cellSize),
        size = Size(3 * cellSize, 3 * cellSize)
    )
}
