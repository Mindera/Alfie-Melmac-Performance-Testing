package repos

import domain.TestMetricOutputResult
import repos.IRepos.ITestMetricOutputResultRepository
import java.sql.Connection

/**
 * Repository implementation for accessing and managing TestMetricOutputResult entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestMetricOutputResultRepository(
    private val connection: Connection
) : ITestMetricOutputResultRepository {

    /**
     * Retrieves all TestMetricOutputResult records from the database.
     *
     * @return A list of all [TestMetricOutputResult] entities.
     */
    override fun findAll(): List<TestMetricOutputResult> {
        val query = """
            SELECT TestMetricOutputResultID, Value, MetricOutputMetricOutputID, TestExecutionTestExecutionID
            FROM TestMetricOutputResult
        """
        val statement = connection.prepareStatement(query)
        val resultSet = statement.executeQuery()

        val results = mutableListOf<TestMetricOutputResult>()
        while (resultSet.next()) {
            results.add(
                TestMetricOutputResult(
                    testMetricOutputResultId = resultSet.getInt("TestMetricOutputResultID"),
                    value = resultSet.getString("Value"),
                    metricOutputMetricOutputId = resultSet.getInt("MetricOutputMetricOutputID"),
                    testExecutionTestExecutionId = resultSet.getInt("TestExecutionTestExecutionID")
                )
            )
        }
        return results
    }

    /**
     * Retrieves all TestMetricOutputResult records for a given TestExecution ID.
     *
     * @param testExecutionId The ID of the TestExecution whose results are to be retrieved.
     * @return A list of [TestMetricOutputResult] entities associated with the given TestExecution ID.
     */
    override fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResult> {
        val query = """
            SELECT TestMetricOutputResultID, Value, MetricOutputMetricOutputID, TestExecutionTestExecutionID
            FROM TestMetricOutputResult
            WHERE TestExecutionTestExecutionID = ?
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, testExecutionId)
        val resultSet = statement.executeQuery()

        val results = mutableListOf<TestMetricOutputResult>()
        while (resultSet.next()) {
            results.add(
                TestMetricOutputResult(
                    testMetricOutputResultId = resultSet.getInt("TestMetricOutputResultID"),
                    value = resultSet.getString("Value"),
                    metricOutputMetricOutputId = resultSet.getInt("MetricOutputMetricOutputID"),
                    testExecutionTestExecutionId = resultSet.getInt("TestExecutionTestExecutionID")
                )
            )
        }
        return results
    }

    /**
     * Saves a new TestMetricOutputResult to the database.
     *
     * @param result The [TestMetricOutputResult] entity to save.
     * @return The generated ID of the inserted TestMetricOutputResult.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(result: TestMetricOutputResult): Int {
        val query = """
            INSERT INTO TestMetricOutputResult (Value, MetricOutputMetricOutputID, TestExecutionTestExecutionID)
            VALUES (?, ?, ?)
        """
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, result.value)
        statement.setInt(2, result.metricOutputMetricOutputId)
        statement.setInt(3, result.testExecutionTestExecutionId)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert TestMetricOutputResult")
    }
}