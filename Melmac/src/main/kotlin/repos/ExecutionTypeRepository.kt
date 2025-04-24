package repos

import domain.ExecutionType
import repos.IRepos.IExecutionTypeRepository

class ExecutionTypeRepository : IExecutionTypeRepository {
    private val types = mutableListOf<ExecutionType>()
    private var nextId = 1

    override fun findById(id: Int): ExecutionType? {
        return types.find { it.id == id }
    }

    override fun findAll(): List<ExecutionType> {
        return types.toList()
    }

    override fun save(type: ExecutionType): Int {
        val withId = type.copy(id = nextId++)
        types.add(withId)
        return withId.id!!
    }
}