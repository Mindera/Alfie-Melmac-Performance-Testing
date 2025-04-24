package repos.IRepos

import domain.ExecutionTypeParameter

interface IExecutionTypeParameterRepository {
    fun getAllByExecutionType(executionTypeId: Int): List<ExecutionTypeParameter>
    fun findById(id: Int): ExecutionTypeParameter?
    fun save(param: ExecutionTypeParameter): Int
    fun saveAll(params: List<ExecutionTypeParameter>)
}