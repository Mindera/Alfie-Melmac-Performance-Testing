package mappers

import domain.Device
import dtos.DeviceResponseDTO

object DeviceMapper {
    fun toDto(device: Device): DeviceResponseDTO {
        return DeviceResponseDTO(
            deviceId = device.deviceId ?: throw IllegalStateException("Device ID cannot be null"),
            deviceName = device.deviceName,
            deviceSerialNumber = device.deviceSerialNumber,
            osVersionOsVersionId = device.osVersionOsVersionId
        )
    }

    fun toDomain(deviceResponseDTO: DeviceResponseDTO): Device {
        return Device(
            deviceId = deviceResponseDTO.deviceId,
            deviceName = deviceResponseDTO.deviceName,
            deviceSerialNumber = deviceResponseDTO.deviceSerialNumber,
            osVersionOsVersionId = deviceResponseDTO.osVersionOsVersionId
        )
    }
}