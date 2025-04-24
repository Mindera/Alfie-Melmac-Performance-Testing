package repos

import domain.TestMetricParameter
import repos.IRepos.ITestMetricParameterRepository

class TestMetricParameterRepository : ITestMetricParameterRepository {
    private val storage = mutableListOf<TestMetricParameter>()
    private var nextId = 1

    override fun save(param: TestMetricParameter): TestMetricParameter {
        val saved = param.copy(id = nextId++)
        storage.add(saved)
        return saved
    }

    override fun saveAll(params: List<TestMetricParameter>) {
        params.forEach { save(it) }
    }

    override fun findByExecutionId(testExecutionId: Int): List<TestMetricParameter> {
        return storage.filter { it.testExecutionId == testExecutionId }
    }
}
