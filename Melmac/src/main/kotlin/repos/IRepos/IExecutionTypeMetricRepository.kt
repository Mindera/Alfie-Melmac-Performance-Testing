package repos.IRepos

/** 
* Repository interface for managing the relationship between execution types and metrics.
*/

interface IExecutionTypeMetricRepository {
    fun getExecutionTypesForMetric(metricId: Int): List<Int>
    fun link(metricId: Int, executionTypeId: Int)
}