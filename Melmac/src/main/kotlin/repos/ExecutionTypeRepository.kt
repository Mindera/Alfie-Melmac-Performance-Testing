package repos

import domain.ExecutionType
import java.sql.Connection
import repos.IRepos.IExecutionTypeRepository

class ExecutionTypeRepository(private val connection: Connection) : IExecutionTypeRepository {

    override fun findById(executionTypeId: Int): ExecutionType? {
        val query = "SELECT ExecutionTypeID, ExecutionTypeName, ExecutionTypeDescription FROM ExecutionType WHERE ExecutionTypeID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, executionTypeId)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            ExecutionType(
                    executionTypeId = resultSet.getInt("ExecutionTypeID"),
                    executionTypeName = resultSet.getString("ExecutionTypeName"),
                    executionTypeDescription =
                            resultSet.getString("ExecutionTypeDescription")
            )
        } else null
    }

    override fun findByMetricId(metricId: Int): List<ExecutionType> {
        val query =
                """
            SELECT et.ExecutionTypeID, et.ExecutionTypeName, et.ExecutionTypeDescription
            FROM ExecutionType et
            INNER JOIN ExecutionType_Metric etm ON etm.ExecutionTypeExecutionTypeID = et.ExecutionTypeID
            WHERE etm.MetricMetricID = ?
        """.trimIndent()

        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        val resultSet = statement.executeQuery()

        val executionTypes = mutableListOf<ExecutionType>()
        while (resultSet.next()) {
            executionTypes.add(
                    ExecutionType(
                            executionTypeId = resultSet.getInt("ExecutionTypeID"),
                            executionTypeName = resultSet.getString("ExecutionTypeName"),
                            executionTypeDescription =
                                    resultSet.getString("ExecutionTypeDescription")
                    )
            )
        }
        return executionTypes
    }

    override fun findByMetricIdAndName(metricId: Int, executionTypeName: String): ExecutionType? {
        val query =
                """
        SELECT et.ExecutionTypeID, et.ExecutionTypeName, et.ExecutionTypeDescription
        FROM ExecutionType et
        INNER JOIN ExecutionType_Metric etm ON etm.ExecutionTypeExecutionTypeID = et.ExecutionTypeID
        WHERE etm.MetricMetricID = ? AND et.ExecutionTypeName = ?
    """.trimIndent()

        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        statement.setString(2, executionTypeName)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            ExecutionType(
                    executionTypeId = resultSet.getInt("ExecutionTypeID"),
                    executionTypeName = resultSet.getString("ExecutionTypeName"),
                    executionTypeDescription = resultSet.getString("ExecutionTypeDescription")
            )
        } else null
    }

    override fun findByName(executionTypeName: String): ExecutionType? {
        val query = "SELECT ExecutionTypeID, ExecutionTypeName, ExecutionTypeDescription FROM ExecutionType WHERE ExecutionTypeName = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, executionTypeName)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            ExecutionType(
                    executionTypeId = resultSet.getInt("ExecutionTypeID"),
                    executionTypeName = resultSet.getString("ExecutionTypeName"),
                    executionTypeDescription =
                            resultSet.getString("ExecutionTypeDescription")
            )
        } else null
    }

    override fun save(executionType: ExecutionType): Int {
        val query =
                "INSERT INTO ExecutionType (ExecutionTypeName, ExecutionTypeDescription) VALUES (?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, executionType.executionTypeName)
        statement.setString(2, executionType.executionTypeDescription)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert ExecutionType")
    }

    override fun update(executionType: ExecutionType) {
        val query =
                "UPDATE ExecutionType SET ExecutionTypeDescription = ? WHERE ExecutionTypeID = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, executionType.executionTypeDescription)
        statement.setInt(2, executionType.executionTypeId!!)
        statement.executeUpdate()
    }
}
