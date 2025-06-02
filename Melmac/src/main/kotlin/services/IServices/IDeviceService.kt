package services.IServices

import dtos.AvailableDeviceDTO

/**
 * Service interface for managing available devices.
 * Provides methods to retrieve device information based on various criteria.
 */
interface IDeviceService {
    fun getAllAvailableDevices(): List<AvailableDeviceDTO>;
    fun getDeviceById(deviceId: Int): AvailableDeviceDTO?;
    fun getAvailableDevicesByMinVersion(minOsVersion: String): List<AvailableDeviceDTO>;
    fun getDeviceBySerialNumber(serialNumber: String): AvailableDeviceDTO?;
    fun getDeviceByName(name: String): AvailableDeviceDTO?;
}
