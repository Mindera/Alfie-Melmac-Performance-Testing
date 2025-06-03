package repos

import domain.TestMetricParameter
import java.sql.Connection
import repos.IRepos.ITestPlanMetricParameterValueRepository

/**
 * Repository implementation for accessing and managing TestMetricParameter entities
 * related to TestPlanVersion in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestPlanMetricParameterValueRepository(private val connection: Connection) :
        ITestPlanMetricParameterValueRepository {

    /**
     * Retrieves all TestMetricParameter records for a given TestPlanVersion ID.
     *
     * @param testPlanVersionId The ID of the TestPlanVersion whose parameters are to be retrieved.
     * @return A list of [TestMetricParameter] entities associated with the given TestPlanVersion ID.
     */
    override fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestMetricParameter> {
        val query =
                """
            SELECT TestMetricParameterID, ParameterValue, MetricParameterMetricParameterID, TestPlanVersionTestPlanVersionID
            FROM TestMetricParameter
            WHERE TestPlanVersionTestPlanVersionID = ?
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, testPlanVersionId)
        val resultSet = statement.executeQuery()

        val params = mutableListOf<TestMetricParameter>()
        while (resultSet.next()) {
            params.add(
                    TestMetricParameter(
                            testMetricParameterId = resultSet.getInt("TestMetricParameterID"),
                            parameterValue = resultSet.getString("ParameterValue"),
                            metricParameterMetricParameterId =
                                    resultSet.getInt("MetricParameterMetricParameterID"),
                            testPlanVersionTestPlanVersionId =
                                    resultSet.getInt("TestPlanVersionTestPlanVersionID")
                    )
            )
        }
        return params
    }

    /**
     * Saves a new TestMetricParameter to the database.
     *
     * @param testMetricParameter The [TestMetricParameter] entity to save.
     * @return The generated ID of the inserted TestMetricParameter.
     * @throws Exception if the insert fails or no key is generated.
     */
    override fun save(testMetricParameter: TestMetricParameter): Int {
        val query =
                """
            INSERT INTO TestMetricParameter (ParameterValue, MetricParameterMetricParameterID, TestPlanVersionTestPlanVersionID)
            VALUES (?, ?, ?)
        """
        connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS).use { statement
            ->
            statement.setString(1, testMetricParameter.parameterValue)
            statement.setInt(2, testMetricParameter.metricParameterMetricParameterId)
            statement.setInt(3, testMetricParameter.testPlanVersionTestPlanVersionId)
            statement.executeUpdate()
            statement.generatedKeys.use { resultSet ->
                return if (resultSet.next()) {
                    resultSet.getInt(1)
                } else {
                    throw Exception("Failed to retrieve generated key for TestMetricParameter")
                }
            }
        }
    }
}