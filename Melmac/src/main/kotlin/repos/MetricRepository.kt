package repos

import domain.Metric
import repos.IRepos.IMetricRepository

class MetricRepository : IMetricRepository {
    private val metrics = mutableListOf<Metric>()
    private var currentId = 1

    override fun findByName(name: String): Metric? =
        metrics.find { it.name.equals(name, ignoreCase = true) }

    override fun findAll(): List<Metric> = metrics

    override fun findById(id: Int): Metric? = metrics.find { it.id == id }

    override fun save(metric: Metric): Int {
        val withId = metric.copy(id = currentId++)
        metrics.add(withId)
        return withId.id!!
    }
}
