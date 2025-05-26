package repos.IRepos

import domain.ExecutionTypeParameter

interface IExecutionTypeParameterRepository {
    fun findById(id: Int): ExecutionTypeParameter?
    fun findByExecutionTypeId(executionTypeId: Int): List<ExecutionTypeParameter>
    fun findByExecutionTypeIdAndName(executionTypeId: Int, parameterName: String): ExecutionTypeParameter?
    fun save(executionTypeParameter: ExecutionTypeParameter): Int
    fun update(executionTypeParameter: ExecutionTypeParameter)
}
