package repos.IRepos

import domain.ExecutionTypeParameter

/**
 * Repository interface for managing ExecutionTypeParameter entities.
 * This interface defines methods for retrieving and manipulating execution type parameters.
 */
interface IExecutionTypeParameterRepository {
    fun findById(id: Int): ExecutionTypeParameter?
    fun findByExecutionTypeId(executionTypeId: Int): List<ExecutionTypeParameter>
    fun findByExecutionTypeIdAndName(executionTypeId: Int, parameterName: String): ExecutionTypeParameter?
    fun save(executionTypeParameter: ExecutionTypeParameter): Int
    fun update(executionTypeParameter: ExecutionTypeParameter)
}
