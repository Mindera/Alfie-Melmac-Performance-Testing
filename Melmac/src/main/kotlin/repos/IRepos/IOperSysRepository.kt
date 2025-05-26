package repos.IRepos

import domain.OperativeSystem

interface IOperSysRepository {
    fun findByName(name: String): OperativeSystem?
    fun findById(id: Int): OperativeSystem?
    fun save(operativeSystem: OperativeSystem): Int
}
