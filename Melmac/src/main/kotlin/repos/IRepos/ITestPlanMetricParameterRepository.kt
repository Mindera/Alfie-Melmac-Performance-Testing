package repos.IRepos

import domain.TestMetricParameter

interface ITestPlanMetricParameterValueRepository {
    fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestMetricParameter>
    fun save(testMetricParameter: TestMetricParameter): Int
}