package repos.IRepos

import domain.TestSuiteVersionPlan

interface ITestSuiteVersionPlanRepository {
    fun findById(id: Int): TestSuiteVersionPlan?
    fun findByTestSuiteVersionId(versionId: Int): List<TestSuiteVersionPlan>
    fun save(testSuiteVersionPlan: TestSuiteVersionPlan): Int
}
