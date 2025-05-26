package repos

import domain.TestSuite
import repos.IRepos.ITestSuiteRepository
import java.sql.Connection
import java.sql.Timestamp

class TestSuiteRepository(
    private val connection: Connection
) : ITestSuiteRepository {

    override fun findAll(): List<TestSuite> {
        val query = "SELECT TestSuiteID, TestSuiteName, TestSuiteDescription, CreationTimestamp, IsActive FROM TestSuite"
        val statement = connection.prepareStatement(query)
        val resultSet = statement.executeQuery()

        val list = mutableListOf<TestSuite>()
        while (resultSet.next()) {
            list.add(
                TestSuite(
                    testSuiteId = resultSet.getInt("TestSuiteID"),
                    testSuiteName = resultSet.getString("TestSuiteName"),
                    testSuiteDescription = resultSet.getString("TestSuiteDescription"),
                    creationTimestamp = resultSet.getTimestamp("CreationTimestamp").toLocalDateTime(),
                    isActive = resultSet.getString("IsActive").toBooleanStrict()
                )
            )
        }
        return list
    }

    override fun findById(id: Int): TestSuite? {
        val query = "SELECT TestSuiteID, TestSuiteName, TestSuiteDescription, CreationTimestamp, IsActive FROM TestSuite WHERE TestSuiteID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TestSuite(
                testSuiteId = resultSet.getInt("TestSuiteID"),
                testSuiteName = resultSet.getString("TestSuiteName"),
                testSuiteDescription = resultSet.getString("TestSuiteDescription"),
                creationTimestamp = resultSet.getTimestamp("CreationTimestamp").toLocalDateTime(),
                isActive = resultSet.getString("IsActive").toBooleanStrict()
            )
        } else null
    }

    override fun save(suite: TestSuite): Int {
        val query = "INSERT INTO TestSuite (TestSuiteName, TestSuiteDescription, CreationTimestamp, IsActive) VALUES (?, ?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, suite.testSuiteName)
        statement.setString(2, suite.testSuiteDescription)
        statement.setTimestamp(3, Timestamp.valueOf(suite.creationTimestamp))
        statement.setString(4, suite.isActive.toString())
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert TestSuite")
    }

    override fun findLatestVersionByTestSuiteId(suiteId: Int): Int? {
        val query = "SELECT MAX(CAST(Version AS INT)) as LatestVersion FROM TestSuiteVersion WHERE TestSuiteTestSuiteID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, suiteId)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) resultSet.getInt("LatestVersion") else null
    }
}
