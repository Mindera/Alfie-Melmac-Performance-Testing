package repos.IRepos

import domain.SuiteExecution

interface ITestSuiteExecutionRepository {
    fun findByTestSuiteVersionId(versionId: Int): List<SuiteExecution>
    fun findById(id: Int): SuiteExecution?
    fun save(execution: SuiteExecution): Int
}
