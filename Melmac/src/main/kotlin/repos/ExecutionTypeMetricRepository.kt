package repos

import repos.IRepos.IExecutionTypeMetricRepository

class ExecutionTypeMetricRepository : IExecutionTypeMetricRepository {
    private val links = mutableListOf<Pair<Int, Int>>() // (metricId, executionTypeId)

    override fun getExecutionTypesForMetric(metricId: Int): List<Int> {
        return links.filter { it.first == metricId }.map { it.second }
    }

    override fun link(metricId: Int, executionTypeId: Int) {
        links.add(metricId to executionTypeId)
    }
}