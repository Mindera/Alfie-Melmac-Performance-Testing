package repos.IRepos

import domain.MetricParameter

interface IMetricParameterRepository {
    fun findById(id: Int): MetricParameter?
    fun findByMetricId(metricId: Int): List<MetricParameter>
    fun findByMetricIdAndName(metricId: Int, parameterName: String): MetricParameter?
    fun save(metricParameter: MetricParameter): Int
    fun update(metricParameter: MetricParameter)
}
