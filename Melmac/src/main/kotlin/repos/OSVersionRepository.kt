package repos

import domain.OSVersion
import repos.IRepos.IOSVersionRepository

class OSVersionRepository : IOSVersionRepository {
    private val versionTable = mutableListOf<OSVersion>()
    private var idCounter = 1

    override fun findAll(): List<OSVersion> = versionTable

    override fun findByVersionNameAndOS(versionName: String, osId: Int): OSVersion? =
        versionTable.find {
            it.versionName == versionName && it.osId == osId
        }

    override fun findById(id: Int): OSVersion? =
        versionTable.find { it.id == id }

    override fun save(version: OSVersion): Int {
        val newVersion = version.copy(id = idCounter++)
        versionTable.add(newVersion)
        return newVersion.id!!
    }
}
