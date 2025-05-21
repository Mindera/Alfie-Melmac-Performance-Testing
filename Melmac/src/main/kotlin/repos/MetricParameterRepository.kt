package repos

import domain.MetricParameter
import repos.IRepos.IMetricParameterRepository

class MetricParameterRepository : IMetricParameterRepository {
    private val params = mutableListOf<MetricParameter>()
    private var currentId = 1

    override fun findByMetricId(metricId: Int): List<MetricParameter> =
        params.filter { it.metricId == metricId }

    override fun findById(id: Int): MetricParameter? =
        params.find { it.id == id }

    override fun save(param: MetricParameter): Int {
        val withId = param.copy(id = currentId++)
        params.add(withId)
        return withId.id!!
    }

    override fun saveAll(params: List<MetricParameter>) {
        params.forEach { save(it) }
    }
}