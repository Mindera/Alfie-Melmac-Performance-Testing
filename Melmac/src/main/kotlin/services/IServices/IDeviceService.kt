package services.IServices

import dtos.AvailableDeviceDTO

interface IDeviceService {
    fun getAllAvailableDevices(): List<AvailableDeviceDTO>;
    fun getAvailableDevicesByMinVersion(minOsVersion: String): List<AvailableDeviceDTO>;
    fun getDeviceBySerialNumber(serialNumber: String): AvailableDeviceDTO?;
    fun getDeviceByName(name: String): AvailableDeviceDTO?;
}
