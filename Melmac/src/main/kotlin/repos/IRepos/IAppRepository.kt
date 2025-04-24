package repos.IRepos

import domain.App

interface IAppRepository {
    fun save(app: App): Int
    fun findById(id: Int): App?
    fun findByName(name: String): App?
    fun findAll(): List<App>
}
