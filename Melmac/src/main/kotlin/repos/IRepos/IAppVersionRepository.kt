package repos.IRepos

import domain.AppVersion

interface IAppVersionRepository {
    fun save(version: AppVersion): Int
    fun findById(id: Int): AppVersion?
    fun findAll(): List<AppVersion>
    fun findByAppId(appId: Int): List<AppVersion>
    fun findByAppIdAndVersionName(appId: Int, versionName: String): AppVersion?
}