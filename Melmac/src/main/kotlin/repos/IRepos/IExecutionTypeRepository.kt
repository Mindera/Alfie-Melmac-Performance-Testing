package repos.IRepos

import domain.ExecutionType

interface IExecutionTypeRepository {
    fun findById(id: Int): ExecutionType?
    fun findAll(): List<ExecutionType>
    fun save(type: ExecutionType): Int
}
