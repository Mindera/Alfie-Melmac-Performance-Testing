package repos.IRepos

import domain.TestThreshold

interface IThresholdRepository {
    fun findById(id: Int): TestThreshold?
    fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestThreshold>
    fun save(testThreshold: TestThreshold): Int
}
