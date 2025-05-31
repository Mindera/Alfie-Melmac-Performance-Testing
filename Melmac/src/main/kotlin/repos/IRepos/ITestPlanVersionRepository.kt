package repos.IRepos

import domain.TestPlanVersion

interface ITestPlanVersionRepository {
    fun findById(id: Int): TestPlanVersion?
    fun findByTestPlanId(testPlanId: Int): List<TestPlanVersion>
    fun findLatestVersionByTestPlanId(testPlanId: Int): TestPlanVersion?
    fun save(testPlanVersion: TestPlanVersion): Int
}
