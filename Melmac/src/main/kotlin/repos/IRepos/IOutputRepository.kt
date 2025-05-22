package repos.IRepos

import domain.Output

interface IOutputRepository {
    fun findByMetricId(metricId: Int): List<Output>
    fun save(output: Output): Int
}