package repos

import domain.TestPlan
import java.sql.Connection
import repos.IRepos.ITestPlanRepository

/**
 * Repository implementation for accessing and managing TestPlan entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestPlanRepository(private val connection: Connection) : ITestPlanRepository {

    /**
     * Finds a TestPlan by its unique identifier.
     *
     * @param testPlanId The ID of the TestPlan to retrieve.
     * @return The [TestPlan] if found, or null otherwise.
     */
    override fun findById(testPlanId: Int): TestPlan? {
        val query = "SELECT TestPlanID, TestName, MetricMetricID FROM TestPlan WHERE TestPlanID = ?"
        connection.prepareStatement(query).use { statement ->
            statement.setInt(1, testPlanId)
            statement.executeQuery().use { resultSet ->
                return if (resultSet.next()) {
                    TestPlan(
                            testPlanId = resultSet.getInt("TestPlanID"),
                            testName = resultSet.getString("TestName"),
                            metricMetricId = resultSet.getInt("MetricMetricID")
                    )
                } else null
            }
        }
    }

    /**
     * Saves a new TestPlan to the database.
     *
     * @param testPlan The [TestPlan] entity to save.
     * @return The generated ID of the inserted TestPlan.
     * @throws Exception if the insert fails or no key is generated.
     */
    override fun save(testPlan: TestPlan): Int {
        val query = "INSERT INTO TestPlan (TestName, MetricMetricID) VALUES (?, ?)"
        connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS).use { statement
            ->
            statement.setString(1, testPlan.testName)
            statement.setInt(2, testPlan.metricMetricId)
            statement.executeUpdate()
            statement.generatedKeys.use { resultSet ->
                return if (resultSet.next()) {
                    resultSet.getInt(1)
                } else {
                    throw Exception("Failed to retrieve generated key for TestPlan")
                }
            }
        }
    }
}