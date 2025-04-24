package repos.IRepos

import domain.MetricParameter

interface IMetricParameterRepository {
    fun findByMetricId(metricId: Int): List<MetricParameter>
    fun findById(id: Int): MetricParameter?
    fun save(param: MetricParameter): Int
    fun saveAll(params: List<MetricParameter>)
}