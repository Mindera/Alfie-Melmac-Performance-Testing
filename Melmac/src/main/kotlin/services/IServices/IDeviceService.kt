package services.IServices

import domain.dtos.OSResponseDTO
import domain.dtos.DeviceResponseDTO
import domain.dtos.OSVersionResponseDTO

interface IDeviceService {
    fun syncAllDevices()
    fun getAllDevices(): List<DeviceResponseDTO>
    fun getAllOSVersions(): List<OSVersionResponseDTO>
    fun getAllOS(): List<OSResponseDTO>
}