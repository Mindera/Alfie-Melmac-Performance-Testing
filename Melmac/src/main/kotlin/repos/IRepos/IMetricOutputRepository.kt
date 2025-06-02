package repos.IRepos

import domain.MetricOutput

/**
 * Repository interface for managing MetricOutput entities.
 * This interface defines methods to interact with the metric output data,
 * including finding outputs by metric ID, saving new outputs, and updating existing ones.
 */
interface IMetricOutputRepository {
    fun findByMetricId(metricId: Int): List<MetricOutput>
    fun findByMetricIdAndName(metricId: Int, outputName: String?): MetricOutput?
    fun save(metricOutput: MetricOutput): Int
    fun update(metricOutput: MetricOutput)
}
