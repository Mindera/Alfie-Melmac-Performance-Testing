package repos.IRepos

import domain.TestSuite

/**
 * Repository interface for managing TestSuite entities.
 */
interface ITestSuiteRepository {
    fun findAll(): List<TestSuite>
    fun findById(id: Int): TestSuite?
    fun save(suite: TestSuite): Int
    fun findLatestVersionByTestSuiteId(suiteId: Int): Int?
}
