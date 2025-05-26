package repos.IRepos

import domain.TestSuiteVersion

interface ITestSuiteVersionRepository {
    fun findLatestVersionByTestSuiteId(suiteId: Int): TestSuiteVersion?
    fun save(version: TestSuiteVersion): Int
}
