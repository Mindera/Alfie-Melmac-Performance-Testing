package repos.IRepos

import domain.Metric

interface IMetricRepository {
    fun findAll(): List<Metric>
    fun findById(metricId: Int): Metric?
    fun findByName(name: String): Metric?
    fun save(metric: Metric): Int
}

