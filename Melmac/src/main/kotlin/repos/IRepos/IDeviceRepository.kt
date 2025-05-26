package repos.IRepos

import domain.Device

interface IDeviceRepository {
    fun findById(id: Int): Device?
    fun save(device: Device): Int
    fun findByName(name: String): Device?
    fun findBySerialNumber(serialNumber: String): Device?
}
