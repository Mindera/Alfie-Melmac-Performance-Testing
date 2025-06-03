package repos.IRepos

import domain.TestSuiteVersionPlan

/**
 * Repository interface for managing TestSuiteVersionPlan entities.
 */
interface ITestSuiteVersionPlanRepository {
    fun findById(id: Int): TestSuiteVersionPlan?
    fun findByTestSuiteVersionId(versionId: Int): List<TestSuiteVersionPlan>
    fun findByTestPlanVersionId(versionId: Int): List<TestSuiteVersionPlan>
    fun save(testSuiteVersionPlan: TestSuiteVersionPlan): Int
}
