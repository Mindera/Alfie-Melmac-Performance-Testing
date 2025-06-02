package repos.IRepos

import domain.TestPlan

/**
 * Repository interface for managing TestPlan entities.
 */
interface ITestPlanRepository {
    fun findById(testPlanId: Int): TestPlan?
    fun save(testPlan: TestPlan): Int
}
