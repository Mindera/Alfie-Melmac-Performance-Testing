package repos.IRepos

import domain.OSVersion

/**
 * Repository interface for managing OSVersion entities.
 */
interface IOperSysVersionRepository {
    fun findById(id: Int): OSVersion?
    fun findByOperSysId(operSysId: Int): List<OSVersion>
    fun save(osVersion: OSVersion): Int
}
