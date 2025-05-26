package repos.IRepos

import domain.ExecutionType

interface IExecutionTypeRepository {
    fun findById(executionTypeId: Int): ExecutionType?
    fun findByMetricId(metricId: Int): List<ExecutionType>
    fun findByMetricIdAndName(metricId: Int, executionTypeName: String): ExecutionType?
    fun findByName(executionTypeName: String): ExecutionType?
    fun save(executionType: ExecutionType): Int
    fun update(executionType: ExecutionType)
}
