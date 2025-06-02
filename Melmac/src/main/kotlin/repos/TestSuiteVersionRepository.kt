package repos

import domain.TestSuiteVersion
import repos.IRepos.ITestSuiteVersionRepository
import java.sql.Connection
import java.sql.Timestamp

/**
 * Repository implementation for accessing and managing TestSuiteVersion entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestSuiteVersionRepository(
    private val connection: Connection
) : ITestSuiteVersionRepository {

    /**
     * Finds the latest TestSuiteVersion for a given TestSuite ID, ordered by version descending.
     *
     * @param suiteId The ID of the TestSuite.
     * @return The latest [TestSuiteVersion] if found, or null otherwise.
     */
    override fun findLatestVersionByTestSuiteId(suiteId: Int): TestSuiteVersion? {
        val query = """
            SELECT TOP 1 * FROM TestSuiteVersion
            WHERE TestSuiteTestSuiteID = ?
            ORDER BY CAST(Version AS INT) DESC
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, suiteId)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TestSuiteVersion(
                testSuiteVersionId = resultSet.getInt("TestSuiteVersionID"),
                version = resultSet.getString("Version"),
                creationTimestamp = resultSet.getTimestamp("CreationTimestamp").toLocalDateTime(),
                notes = resultSet.getString("Notes"),
                testSuiteTestSuiteId = resultSet.getInt("TestSuiteTestSuiteID")
            )
        } else null
    }

    /**
     * Saves a new TestSuiteVersion to the database.
     *
     * @param version The [TestSuiteVersion] entity to save.
     * @return The generated ID of the inserted TestSuiteVersion.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(version: TestSuiteVersion): Int {
        val query = "INSERT INTO TestSuiteVersion (Version, CreationTimestamp, Notes, TestSuiteTestSuiteID) VALUES (?, ?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, version.version)
        statement.setTimestamp(2, Timestamp.valueOf(version.creationTimestamp))
        statement.setString(3, version.notes)
        statement.setInt(4, version.testSuiteTestSuiteId)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert TestSuiteVersion")
    }
}