package repos.IRepos

import domain.OSVersion

interface IOSVersionRepository {
    fun findAll(): List<OSVersion>
    fun findByVersionNameAndOS(versionName: String, osId: Int): OSVersion?
    fun findById(id: Int): OSVersion?
    fun save(version: OSVersion): Int
}