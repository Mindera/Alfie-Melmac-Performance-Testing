package repos.IRepos

import domain.Metric

/**
 * Repository interface for managing Metric entities.
 */
interface IMetricRepository {
    fun findAll(): List<Metric>
    fun findById(metricId: Int): Metric?
    fun findByName(name: String): Metric?
    fun save(metric: Metric): Int
}

