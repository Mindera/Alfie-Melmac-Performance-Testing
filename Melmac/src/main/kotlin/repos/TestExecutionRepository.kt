package repos

import repos.IRepos.ITestExecutionRepository
import domain.TestExecution

class TestExecutionRepository : ITestExecutionRepository {
    private val storage = mutableListOf<TestExecution>()
    private var nextId = 1

    override fun save(execution: TestExecution): Int {
        val withId = execution.copy(id = nextId++)
        storage.add(withId)
        return withId.id
    }

    override fun findAll(): List<TestExecution> = storage
    override fun findById(id: Int): TestExecution? = storage.find { it.id == id }
    override fun findBySuiteId(testSuiteId: Int): List<TestExecution> =
        storage.filter { it.testSuiteId == testSuiteId }
}
