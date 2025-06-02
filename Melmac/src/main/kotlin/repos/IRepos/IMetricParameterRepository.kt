package repos.IRepos

import domain.MetricParameter

/**
 * Repository interface for managing MetricParameter entities.
 * This interface defines methods for retrieving and saving metric parameters associated with metrics.
 */
interface IMetricParameterRepository {
    fun findById(id: Int): MetricParameter?
    fun findByMetricId(metricId: Int): List<MetricParameter>
    fun findByMetricIdAndName(metricId: Int, parameterName: String): MetricParameter?
    fun save(metricParameter: MetricParameter): Int
    fun update(metricParameter: MetricParameter)
}
