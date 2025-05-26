package repos.IRepos

import domain.OSVersion

interface IOperSysVersionRepository {
    fun findById(id: Int): OSVersion?
    fun findByOperSysId(operSysId: Int): List<OSVersion>
    fun save(osVersion: OSVersion): Int
}
