package repos.IRepos

import domain.SuiteExecution

/**
 * Repository interface for managing SuiteExecution entities.
 */
interface ITestSuiteExecutionRepository {
    fun findByTestSuiteVersionId(versionId: Int): List<SuiteExecution>
    fun findById(id: Int): SuiteExecution?
    fun save(execution: SuiteExecution): Int
}
