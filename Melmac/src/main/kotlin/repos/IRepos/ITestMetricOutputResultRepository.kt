package repos.IRepos

import domain.TestMetricOutputResult

interface ITestMetricOutputResultRepository {
    fun findAll(): List<TestMetricOutputResult>
    fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResult>
    // Additional methods can be added here as needed
    fun save(result: TestMetricOutputResult): Int
}
