package repos

import domain.SuiteExecution
import repos.IRepos.ITestSuiteExecutionRepository
import java.sql.Connection
import java.sql.Timestamp

/**
 * Repository implementation for accessing and managing SuiteExecution entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestSuiteExecutionRepository(
    private val connection: Connection
) : ITestSuiteExecutionRepository {

    /**
     * Finds a SuiteExecution by its unique identifier.
     *
     * @param id The ID of the SuiteExecution to retrieve.
     * @return The [SuiteExecution] if found, or null otherwise.
     */
    override fun findById(id: Int): SuiteExecution? {
        val query = "SELECT SuiteExecutionID, InitialTimestamp, EndTimestamp, TestSuiteVersionTestSuiteVersionID FROM SuiteExecution WHERE SuiteExecutionID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            SuiteExecution(
                suiteExecutionId = resultSet.getInt("SuiteExecutionID"),
                initialTimestamp = resultSet.getTimestamp("InitialTimestamp").toLocalDateTime(),
                endTimestamp = resultSet.getTimestamp("EndTimestamp").toLocalDateTime(),
                testSuiteVersionTestSuiteVersionId = resultSet.getInt("TestSuiteVersionTestSuiteVersionID")
            )
        } else null
    }

    /**
     * Retrieves all SuiteExecution records for a given TestSuiteVersion ID, ordered by initial timestamp descending.
     *
     * @param versionId The ID of the TestSuiteVersion whose executions are to be retrieved.
     * @return A list of [SuiteExecution] entities associated with the given TestSuiteVersion ID.
     */
    override fun findByTestSuiteVersionId(versionId: Int): List<SuiteExecution> {
        val query = """
            SELECT SuiteExecutionID, InitialTimestamp, EndTimestamp, TestSuiteVersionTestSuiteVersionID
            FROM SuiteExecution
            WHERE TestSuiteVersionTestSuiteVersionID = ?
            ORDER BY InitialTimestamp DESC
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, versionId)
        val resultSet = statement.executeQuery()

        val list = mutableListOf<SuiteExecution>()
        while (resultSet.next()) {
            list.add(
                SuiteExecution(
                    suiteExecutionId = resultSet.getInt("SuiteExecutionID"),
                    initialTimestamp = resultSet.getTimestamp("InitialTimestamp").toLocalDateTime(),
                    endTimestamp = resultSet.getTimestamp("EndTimestamp").toLocalDateTime(),
                    testSuiteVersionTestSuiteVersionId = resultSet.getInt("TestSuiteVersionTestSuiteVersionID")
                )
            )
        }
        return list
    }

    /**
     * Saves a new SuiteExecution to the database.
     *
     * @param execution The [SuiteExecution] entity to save.
     * @return The generated ID of the inserted SuiteExecution.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(execution: SuiteExecution): Int {
        val query = "INSERT INTO SuiteExecution (InitialTimestamp, EndTimestamp, TestSuiteVersionTestSuiteVersionID) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setTimestamp(1, Timestamp.valueOf(execution.initialTimestamp))
        statement.setTimestamp(2, Timestamp.valueOf(execution.endTimestamp))
        statement.setInt(3, execution.testSuiteVersionTestSuiteVersionId)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert SuiteExecution")
    }
}