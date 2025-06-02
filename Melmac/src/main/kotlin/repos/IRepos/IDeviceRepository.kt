package repos.IRepos

import domain.Device

/**
 * Repository interface for managing Device entities.
 */
interface IDeviceRepository {
    fun findById(id: Int): Device?
    fun save(device: Device): Int
    fun findByName(name: String): Device?
    fun findBySerialNumber(serialNumber: String): Device?
}
