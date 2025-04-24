package repos

import repos.IRepos.IAppVersionRepository
import domain.AppVersion

class AppVersionRepository : IAppVersionRepository {
    private val storage = mutableListOf<AppVersion>()
    private var nextId = 1

    override fun save(version: AppVersion): Int {
        val withId = version.copy(id = nextId++)
        storage.add(withId)
        return withId.id!!
    }

    override fun findById(id: Int): AppVersion? = storage.find { it.id == id }

    override fun findAll(): List<AppVersion> = storage

    override fun findByAppId(appId: Int): List<AppVersion> =
        storage.filter { it.appId == appId }

    override fun findByAppIdAndVersionName(appId: Int, versionName: String): AppVersion? =
        storage.find { it.appId == appId && it.versionName.equals(versionName, ignoreCase = true) }
}
