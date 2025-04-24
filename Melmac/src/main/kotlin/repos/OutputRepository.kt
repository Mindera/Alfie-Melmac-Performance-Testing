package repos

import domain.Output
import repos.IRepos.IOutputRepository

class OutputRepository : IOutputRepository {
    private val outputs = mutableListOf<Output>()
    private var currentId = 1

    override fun findByMetricId(metricId: Int): List<Output> =
        outputs.filter { it.metricId == metricId }

    override fun save(output: Output): Int {
        val withId = output.copy(id = currentId++)
        outputs.add(withId)
        return withId.id!!
    }
}
