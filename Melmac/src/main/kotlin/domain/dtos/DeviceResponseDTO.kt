package dtos

data class DeviceResponseDTO(
    val deviceId: Int,
    val deviceName: String,
    val deviceSerialNumber: String? = null,
    val osVersionOsVersionId: Int
)
