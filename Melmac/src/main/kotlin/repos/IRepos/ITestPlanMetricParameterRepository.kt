package repos.IRepos

import domain.TestMetricParameter

/**
 * Repository interface for managing TestMetricParameter entities.
 */
interface ITestPlanMetricParameterValueRepository {
    fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestMetricParameter>
    fun save(testMetricParameter: TestMetricParameter): Int
}