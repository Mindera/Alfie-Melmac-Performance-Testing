package repos.IRepos

import domain.TestSuite

interface ITestSuiteRepository {
    fun save(suite: TestSuite): Int
    fun findAll(): List<TestSuite>
    fun findById(id: Int): TestSuite?
}