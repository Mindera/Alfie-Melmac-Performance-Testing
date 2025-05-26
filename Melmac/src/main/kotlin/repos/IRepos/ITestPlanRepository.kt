package repos.IRepos

import domain.TestPlan

interface ITestPlanRepository {
    fun findById(testPlanId: Int): TestPlan?
    fun save(testPlan: TestPlan): Int
}
