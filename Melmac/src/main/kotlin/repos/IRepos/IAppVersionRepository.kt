package repos.IRepos

import domain.AppVersion

interface IAppVersionRepository {
    fun findByAppId(appId: Int): List<AppVersion>
    fun findById(id: Int): AppVersion?
    fun findByName(appVersion: String): AppVersion?
    fun findByAppIdAndVersion(appId: Int, version: String): AppVersion?
    fun save(appVersion: AppVersion): Int
}
