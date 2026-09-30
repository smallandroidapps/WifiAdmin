package com.location.wifiadmin.data

data class GuestNetwork(
    val ssid: String = "",
    val password: String = "",
    val isActive: Boolean = false,
    val durationHours: Int = 2,
    val expirationTimestamp: Long = 0L
) {
    val remainingMinutesText: String
        get() {
            if (!isActive || expirationTimestamp <= 0L) return "Inactive"
            val diffMs = expirationTimestamp - System.currentTimeMillis()
            if (diffMs <= 0) return "Expired"
            val mins = diffMs / (1000 * 60)
            val hours = mins / 60
            val remMins = mins % 60
            return if (hours > 0) "${hours}h ${remMins}m remaining" else "${remMins}m remaining"
        }
}
