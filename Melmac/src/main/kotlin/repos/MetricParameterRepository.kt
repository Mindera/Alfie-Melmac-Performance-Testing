package repos

import domain.MetricParameter
import java.sql.Connection
import repos.IRepos.IMetricParameterRepository

/**
 * Repository implementation for accessing and managing MetricParameter entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class MetricParameterRepository(private val connection: Connection) : IMetricParameterRepository {

    /**
     * Finds a MetricParameter by its unique identifier.
     *
     * @param id The ID of the MetricParameter to retrieve.
     * @return The [MetricParameter] if found, or null otherwise.
     */
    override fun findById(id: Int): MetricParameter? {
        val query =
                "SELECT MetricParameterID, ParameterName, ParameterType, MetricMetricID FROM MetricParameter WHERE MetricParameterID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            MetricParameter(
                    metricParameterId = resultSet.getInt("MetricParameterID"),
                    parameterName = resultSet.getString("ParameterName"),
                    parameterType = resultSet.getString("ParameterType"),
                    metricMetricId = resultSet.getInt("MetricMetricID")
            )
        } else null
    }

    /**
     * Retrieves all MetricParameter records for a given Metric ID.
     *
     * @param metricId The ID of the Metric whose parameters are to be retrieved.
     * @return A list of [MetricParameter] entities associated with the given Metric ID.
     */
    override fun findByMetricId(metricId: Int): List<MetricParameter> {
        val query =
                "SELECT MetricParameterID, ParameterName, ParameterType, MetricMetricID FROM MetricParameter WHERE MetricMetricID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        val resultSet = statement.executeQuery()

        val parameters = mutableListOf<MetricParameter>()
        while (resultSet.next()) {
            parameters.add(
                    MetricParameter(
                            metricParameterId = resultSet.getInt("MetricParameterID"),
                            parameterName = resultSet.getString("ParameterName"),
                            parameterType = resultSet.getString("ParameterType"),
                            metricMetricId = resultSet.getInt("MetricMetricID")
                    )
            )
        }
        return parameters
    }

    /**
     * Finds a MetricParameter by Metric ID and parameter name.
     *
     * @param metricId The ID of the Metric.
     * @param parameterName The name of the parameter to retrieve.
     * @return The [MetricParameter] if found, or null otherwise.
     */
    override fun findByMetricIdAndName(metricId: Int, parameterName: String): MetricParameter? {
        val query =
                "SELECT MetricParameterID, ParameterName, ParameterType, MetricMetricID FROM MetricParameter WHERE MetricMetricID = ? AND ParameterName = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        statement.setString(2, parameterName)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            MetricParameter(
                    metricParameterId = resultSet.getInt("MetricParameterID"),
                    parameterName = resultSet.getString("ParameterName"),
                    parameterType = resultSet.getString("ParameterType"),
                    metricMetricId = resultSet.getInt("MetricMetricID")
            )
        } else null
    }

    /**
     * Saves a new MetricParameter to the database.
     *
     * @param metricParameter The [MetricParameter] entity to save.
     * @return The generated ID of the inserted MetricParameter.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(metricParameter: MetricParameter): Int {
        val query =
                "INSERT INTO MetricParameter (ParameterName, ParameterType, MetricMetricID) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, metricParameter.parameterName)
        statement.setString(2, metricParameter.parameterType)
        statement.setInt(3, metricParameter.metricMetricId)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert MetricParameter")
    }

    /**
     * Updates the parameter type of an existing MetricParameter.
     *
     * @param metricParameter The [MetricParameter] entity with updated information.
     */
    override fun update(metricParameter: MetricParameter) {
        val query = "UPDATE MetricParameter SET ParameterType = ? WHERE MetricParameterID = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, metricParameter.parameterType)
        statement.setInt(2, metricParameter.metricParameterId!!)
        statement.executeUpdate()
    }
}