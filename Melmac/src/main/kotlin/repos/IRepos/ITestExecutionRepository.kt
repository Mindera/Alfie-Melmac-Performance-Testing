package repos.IRepos

import domain.TestExecution

interface ITestExecutionRepository {
    fun findAll(): List<TestExecution>
    fun findById(id: Int): TestExecution?
    fun save(testExecution: TestExecution): Int
}
