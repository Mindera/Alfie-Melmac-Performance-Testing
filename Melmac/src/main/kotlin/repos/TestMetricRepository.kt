package repositories

import domain.TestMetric
import repos.IRepos.ITestMetricRepository

class TestMetricRepository : ITestMetricRepository {
    private val metrics = mutableListOf<TestMetric>()
    private var nextId = 1

    override suspend fun getAll(): List<TestMetric> = metrics

    override suspend fun getById(id: Int): TestMetric? = metrics.find { it.id == id }

    override suspend fun getByExecutionId(testExecutionId: Int): List<TestMetric> =
        metrics.filter { it.testExecutionId == testExecutionId }

    override suspend fun create(metric: TestMetric): TestMetric {
        val created = metric.copy(id = nextId++)
        metrics.add(created)
        return created
    }

    override suspend fun deleteById(id: Int) {
        metrics.removeIf { it.id == id }
    }
}
