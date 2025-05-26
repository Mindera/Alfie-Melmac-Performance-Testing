package domain

data class Device(
    val deviceId: Int? = null,
    val deviceName: String,
    val deviceSerialNumber: String? = null,
    val osVersionOsVersionId: Int
)
