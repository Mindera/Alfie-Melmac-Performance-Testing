package repos.IRepos

import domain.TestExecution

interface ITestExecutionRepository {
    fun save(execution: TestExecution): Int
    fun findAll(): List<TestExecution>
    fun findById(id: Int): TestExecution?
    fun findBySuiteId(testSuiteId: Int): List<TestExecution>
}
