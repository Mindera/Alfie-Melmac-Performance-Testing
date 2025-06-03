package repos.IRepos

import domain.ExecutionType

/**
 * Repository interface for managing ExecutionType entities.
 * This interface defines methods for retrieving and saving execution types,
 * which are used to categorize different execution modes of metrics.
 */
interface IExecutionTypeRepository {
    fun findById(executionTypeId: Int): ExecutionType?
    fun findByMetricId(metricId: Int): List<ExecutionType>
    fun findByMetricIdAndName(metricId: Int, executionTypeName: String): ExecutionType?
    fun findByName(executionTypeName: String): ExecutionType?
    fun save(executionType: ExecutionType): Int
    fun update(executionType: ExecutionType)
}
