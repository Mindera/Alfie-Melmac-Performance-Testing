package mappers

import dtos.AvailableDeviceDTO
import domain.Device

object AvailableDeviceMapper {
    fun toDto(device: Device, osName: String, osVersion: String): AvailableDeviceDTO {
        return AvailableDeviceDTO(
            id = device.deviceId,
            deviceName = device.deviceName,
            deviceSerialNumber = device.deviceSerialNumber,
            osName = osName,
            osVersion = osVersion
        )
    }

    fun toDomain(availableDeviceDTO: AvailableDeviceDTO, osVersionOsVersionId: Int): Device {
        return Device(
            deviceId = availableDeviceDTO.id,
            deviceName = availableDeviceDTO.deviceName,
            deviceSerialNumber = availableDeviceDTO.deviceSerialNumber,
            osVersionOsVersionId = osVersionOsVersionId
        )
    }
}