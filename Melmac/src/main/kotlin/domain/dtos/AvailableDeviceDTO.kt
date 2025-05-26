package dtos

data class AvailableDeviceDTO(
    val id: Int?,
    val deviceName: String,
    val deviceSerialNumber: String? = null,
    val osName: String,
    val osVersion: String
)
