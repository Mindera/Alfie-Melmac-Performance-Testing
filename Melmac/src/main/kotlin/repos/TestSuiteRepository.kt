package repos

import repos.IRepos.ITestSuiteRepository
import domain.TestSuite

class TestSuiteRepository : ITestSuiteRepository {
    private val storage = mutableListOf<TestSuite>()
    private var nextId = 1

    override fun save(suite: TestSuite): Int {
        val withId = suite.copy(id = nextId++)
        storage.add(withId)
        return withId.id!!
    }

    override fun findAll(): List<TestSuite> = storage

    override fun findById(id: Int): TestSuite? = storage.find { it.id == id }
}