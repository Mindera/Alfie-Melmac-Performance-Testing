package repos.IRepos

import domain.OperativeSystem

/**
 * Repository interface for managing OperativeSystem entities.
 */
interface IOperSysRepository {
    fun findByName(name: String): OperativeSystem?
    fun findById(id: Int): OperativeSystem?
    fun save(operativeSystem: OperativeSystem): Int
}
