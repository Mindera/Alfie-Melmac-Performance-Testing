package repos

import domain.Device
import repos.IRepos.IDeviceRepository

class DeviceRepository : IDeviceRepository {
    private val devices = mutableListOf<Device>()

    override fun findAll(): List<Device> = devices

    override fun findById(id: String): Device? =
        devices.find { it.id == id }

    override fun save(device: Device) {
        devices.add(device)
    }
}