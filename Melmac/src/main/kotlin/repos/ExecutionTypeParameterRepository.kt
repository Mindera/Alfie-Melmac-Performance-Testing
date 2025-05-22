package repos

import domain.ExecutionTypeParameter
import repos.IRepos.IExecutionTypeParameterRepository

class ExecutionTypeParameterRepository : IExecutionTypeParameterRepository {
    private val params = mutableListOf<ExecutionTypeParameter>()
    private var currentId = 1

    override fun getAllByExecutionType(executionTypeId: Int): List<ExecutionTypeParameter> {
        return params.filter { it.executionTypeId == executionTypeId }
    }

    override fun findById(id: Int): ExecutionTypeParameter? {
        return params.find { it.id == id }
    }

    override fun save(param: ExecutionTypeParameter): Int {
        val withId = param.copy(id = currentId++)
        params.add(withId)
        return withId.id!!
    }

    override fun saveAll(params: List<ExecutionTypeParameter>) {
        params.forEach { save(it) }
    }
}