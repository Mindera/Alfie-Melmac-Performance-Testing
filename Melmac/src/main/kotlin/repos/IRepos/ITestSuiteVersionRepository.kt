package repos.IRepos

import domain.TestSuiteVersion

/**
 * Repository interface for managing TestSuiteVersion entities.
 */
interface ITestSuiteVersionRepository {
    fun findLatestVersionByTestSuiteId(suiteId: Int): TestSuiteVersion?
    fun save(version: TestSuiteVersion): Int
}
