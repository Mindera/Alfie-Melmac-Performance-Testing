package repos.IRepos

import domain.TestMetric

interface ITestMetricRepository {
    suspend fun getAll(): List<TestMetric>
    suspend fun getById(id: Int): TestMetric?
    suspend fun getByExecutionId(testExecutionId: Int): List<TestMetric>
    suspend fun create(metric: TestMetric): TestMetric
    suspend fun deleteById(id: Int)
}
