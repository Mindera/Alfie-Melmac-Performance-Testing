package repos.IRepos

import domain.MetricOutput

interface IMetricOutputRepository {
    fun findByMetricId(metricId: Int): List<MetricOutput>
    fun findByMetricIdAndName(metricId: Int, outputName: String?): MetricOutput?
    fun save(metricOutput: MetricOutput): Int
    fun update(metricOutput: MetricOutput)
}
