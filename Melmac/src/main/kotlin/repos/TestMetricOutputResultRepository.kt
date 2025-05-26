package repos

import domain.TestMetricOutputResult
import repos.IRepos.ITestMetricOutputResultRepository
import java.sql.Connection

class TestMetricOutputResultRepository(
    private val connection: Connection
) : ITestMetricOutputResultRepository {

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
