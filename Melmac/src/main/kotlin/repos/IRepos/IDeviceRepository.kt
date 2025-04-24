package repos.IRepos

import domain.Device

interface IDeviceRepository {
    fun findAll(): List<Device>
    fun findById(id: String): Device?
    fun save(device: Device)
}