package repos.IRepos

import domain.TestMetricParameter

interface ITestMetricParameterRepository {
    fun save(param: TestMetricParameter): TestMetricParameter
    fun saveAll(params: List<TestMetricParameter>)
    fun findByExecutionId(testExecutionId: Int): List<TestMetricParameter>
}