package repos.IRepos

import domain.App

/**
 * Repository interface for managing App entities.
 */
interface IAppRepository {
    fun findAll(): List<App>
    fun findById(appId: Int): App?
    fun findByName(appName: String): App?
    fun save(app: App): Int
}