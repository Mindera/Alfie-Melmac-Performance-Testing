package repos.IRepos

interface IExecutionTypeMetricRepository {
    fun getExecutionTypesForMetric(metricId: Int): List<Int>
    fun link(metricId: Int, executionTypeId: Int)
}