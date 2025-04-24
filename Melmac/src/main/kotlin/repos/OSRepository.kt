package repos

import domain.OS
import repos.IRepos.IOSRepository

class OSRepository : IOSRepository {
    private val osTable = mutableListOf<OS>()
    private var idCounter = 1

    override fun findAll(): List<OS> = osTable

    override fun findByName(name: String): OS? =
        osTable.find { it.name.equals(name, ignoreCase = true) }

    override fun save(os: OS): Int {
        val newOS = os.copy(id = idCounter++)
        osTable.add(newOS)
        return newOS.id!!
    }
}
