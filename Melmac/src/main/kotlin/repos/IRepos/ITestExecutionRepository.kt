package repos.IRepos

import domain.TestExecution

/**
 * Repository interface for managing TestExecution entities.
 */
interface ITestExecutionRepository {
    fun findAll(): List<TestExecution>
    fun findById(id: Int): TestExecution?
    fun save(testExecution: TestExecution): Int
}
