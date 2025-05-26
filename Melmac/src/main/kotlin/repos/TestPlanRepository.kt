package repos

import domain.TestPlan
import java.sql.Connection
import repos.IRepos.ITestPlanRepository

class TestPlanRepository(private val connection: Connection) : ITestPlanRepository {
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
