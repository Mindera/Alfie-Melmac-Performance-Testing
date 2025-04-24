package repos

import domain.TestExecutionTypeParameter
import repos.IRepos.ITestExecutionTypeParameterRepository

class TestExecutionTypeParameterRepository : ITestExecutionTypeParameterRepository {
    private val storage = mutableListOf<TestExecutionTypeParameter>()
    private var nextId = 1

    override fun save(param: TestExecutionTypeParameter): TestExecutionTypeParameter {
        val saved = param.copy(id = nextId++)
        storage.add(saved)
        return saved
    }

    override fun saveAll(params: List<TestExecutionTypeParameter>) {
        params.forEach { save(it) }
    }

    override fun findByExecutionId(executionId: Int): List<TestExecutionTypeParameter> {
        return storage.filter { it.executionId == executionId }
    }
}
