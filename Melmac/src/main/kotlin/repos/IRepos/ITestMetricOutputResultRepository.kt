package repos.IRepos

import domain.TestMetricOutputResult

/**
 * Repository interface for managing TestMetricOutputResult entities.
 */
interface ITestMetricOutputResultRepository {
    fun findAll(): List<TestMetricOutputResult>
    fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResult>
    fun save(result: TestMetricOutputResult): Int
}
