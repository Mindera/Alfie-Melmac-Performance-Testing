package repos

import domain.TestSuite
import repos.IRepos.ITestSuiteRepository
import java.sql.Connection
import java.sql.Timestamp

/**
 * Repository implementation for accessing and managing TestSuite entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestSuiteRepository(
    private val connection: Connection
) : ITestSuiteRepository {

    /**
     * Retrieves all TestSuite records from the database.
     *
     * @return A list of all [TestSuite] entities.
     */
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

    /**
     * Finds a TestSuite by its unique identifier.
     *
     * @param id The ID of the TestSuite to retrieve.
     * @return The [TestSuite] if found, or null otherwise.
     */
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

    /**
     * Saves a new TestSuite to the database.
     *
     * @param suite The [TestSuite] entity to save.
     * @return The generated ID of the inserted TestSuite.
     * @throws IllegalStateException if the insert fails.
     */
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

    /**
     * Finds the latest version number for a given TestSuite ID.
     *
     * @param suiteId The ID of the TestSuite.
     * @return The latest version number as [Int], or null if not found.
     */
    override fun findLatestVersionByTestSuiteId(suiteId: Int): Int? {
        val query = "SELECT MAX(CAST(Version AS INT)) as LatestVersion FROM TestSuiteVersion WHERE TestSuiteTestSuiteID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, suiteId)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) resultSet.getInt("LatestVersion") else null
    }
}