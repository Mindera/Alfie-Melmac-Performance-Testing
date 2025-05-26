package repos

import domain.TestThreshold
import repos.IRepos.IThresholdRepository
import java.sql.Connection

class ThresholdRepository(
    private val connection: Connection
) : IThresholdRepository {

    override fun findById(id: Int): TestThreshold? {
        val query = "SELECT TestThresholdID, TargetValue, ThresholdTypeThresholdTypeID, TestPlanVersionTestPlanVersionID, MetricOutputMetricOutputID FROM TestThreshold WHERE TestThresholdID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TestThreshold(
                testThresholdId = resultSet.getInt("TestThresholdID"),
                targetValue = resultSet.getInt("TargetValue"),
                thresholdTypeThresholdTypeId = resultSet.getInt("ThresholdTypeThresholdTypeID"),
                testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID"),
                metricOutputMetricOutputId = resultSet.getInt("MetricOutputMetricOutputID")
            )
        } else null
    }

    override fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestThreshold> {
        val query = """
            SELECT TestThresholdID, TargetValue, ThresholdTypeThresholdTypeID, TestPlanVersionTestPlanVersionID, MetricOutputMetricOutputID
            FROM TestThreshold
            WHERE TestPlanVersionTestPlanVersionID = ?
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, testPlanVersionId)
        val resultSet = statement.executeQuery()

        val thresholds = mutableListOf<TestThreshold>()
        while (resultSet.next()) {
            thresholds.add(
                TestThreshold(
                    testThresholdId = resultSet.getInt("TestThresholdID"),
                    targetValue = resultSet.getInt("TargetValue"),
                    thresholdTypeThresholdTypeId = resultSet.getInt("ThresholdTypeThresholdTypeID"),
                    testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID"),
                    metricOutputMetricOutputId = resultSet.getInt("MetricOutputMetricOutputID")
                )
            )
        }
        return thresholds
    }

    override fun save(testThreshold: TestThreshold): Int {
        val query = """
            INSERT INTO TestThreshold (TargetValue, ThresholdTypeThresholdTypeID, TestPlanVersionTestPlanVersionID, MetricOutputMetricOutputID)
            VALUES (?, ?, ?, ?)
        """
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setInt(1, testThreshold.targetValue)
        statement.setInt(2, testThreshold.thresholdTypeThresholdTypeId)
        statement.setInt(3, testThreshold.testPlanVersionTestPlanVersionId)
        statement.setInt(4, testThreshold.metricOutputMetricOutputId)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys
        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else -1
    }
}
