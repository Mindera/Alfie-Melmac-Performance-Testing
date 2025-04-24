package repos

import repos.IRepos.IAppRepository
import domain.App

class AppRepository : IAppRepository {
    private val storage = mutableListOf<App>()
    private var nextId = 1

    override fun save(app: App): Int {
        val withId = app.copy(id = nextId++)
        storage.add(withId)
        return withId.id!!
    }

    override fun findById(id: Int): App? = storage.find { it.id == id }
    override fun findByName(name: String): App? =
        storage.find { it.name.equals(name, ignoreCase = true) }

    override fun findAll(): List<App> = storage
}
