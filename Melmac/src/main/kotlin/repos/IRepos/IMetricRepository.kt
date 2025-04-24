package repos.IRepos

import domain.Metric

interface IMetricRepository {
    fun findAll(): List<Metric>
    fun findById(id: Int): Metric?
    fun findByName(name: String): Metric?
    fun save(metric: Metric): Int
}
