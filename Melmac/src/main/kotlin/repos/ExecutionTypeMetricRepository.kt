package repos

import repos.IRepos.IExecutionTypeMetricRepository
import domain.ExecutionTypeMetric
import java.sql.Connection

/**
 * Repository implementation for managing the relationship between ExecutionType and Metric entities.
 *
 * @property connection The JDBC connection used for database operations.
 */
class ExecutionTypeMetricRepository(
    private val connection: Connection
) : IExecutionTypeMetricRepository {

    /**
     * Retrieves all ExecutionType IDs linked to a specific Metric.
     *
     * @param metricId The ID of the Metric.
     * @return A list of ExecutionType IDs associated with the given Metric.
     */
    override fun getExecutionTypesForMetric(metricId: Int): List<Int> {
        val stmt = connection.prepareStatement(
            "SELECT ExecutionTypeExecutionTypeID FROM ExecutionType_Metric WHERE MetricMetricID = ?"
        )
        stmt.setInt(1, metricId)
        val rs = stmt.executeQuery()
        val result = mutableListOf<Int>()
        while (rs.next()) {
            result.add(rs.getInt("ExecutionTypeExecutionTypeID"))
        }
        rs.close()
        stmt.close()
        return result
    }

    /**
     * Links an ExecutionType to a Metric if the association does not already exist.
     *
     * @param metricId The ID of the Metric.
     * @param executionTypeId The ID of the ExecutionType.
     */
    override fun link(metricId: Int, executionTypeId: Int) {
        // Check if the link already exists
        val checkStmt = connection.prepareStatement(
            "SELECT COUNT(*) FROM ExecutionType_Metric WHERE MetricMetricID = ? AND ExecutionTypeExecutionTypeID = ?"
        )
        checkStmt.setInt(1, metricId)
        checkStmt.setInt(2, executionTypeId)
        val rs = checkStmt.executeQuery()
        rs.next()
        val exists = rs.getInt(1) > 0
        rs.close()
        checkStmt.close()

        if (!exists) {
            val insertStmt = connection.prepareStatement(
                "INSERT INTO ExecutionType_Metric (MetricMetricID, ExecutionTypeExecutionTypeID) VALUES (?, ?)"
            )
            insertStmt.setInt(1, metricId)
            insertStmt.setInt(2, executionTypeId)
            insertStmt.executeUpdate()
            insertStmt.close()
        }
    }
}