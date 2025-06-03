package repos

import domain.MetricOutput
import java.sql.Connection
import repos.IRepos.IMetricOutputRepository

/**
 * Repository implementation for accessing and managing MetricOutput entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class MetricOutputRepository(private val connection: Connection) : IMetricOutputRepository {

    /**
     * Retrieves all MetricOutput records for a given Metric ID.
     *
     * @param metricId The ID of the Metric whose outputs are to be retrieved.
     * @return A list of [MetricOutput] entities associated with the given Metric ID.
     */
    override fun findByMetricId(metricId: Int): List<MetricOutput> {
        val query =
                "SELECT MetricOutputID, OutputName, Unit, MetricMetricID FROM MetricOutput WHERE MetricMetricID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        val resultSet = statement.executeQuery()

        val outputs = mutableListOf<MetricOutput>()
        while (resultSet.next()) {
            outputs.add(
                    MetricOutput(
                            metricOutputId = resultSet.getInt("MetricOutputID"),
                            outputName = resultSet.getString("OutputName"),
                            unit = resultSet.getString("Unit"),
                            metricMetricId = resultSet.getInt("MetricMetricID")
                    )
            )
        }
        return outputs
    }

    /**
     * Finds a MetricOutput by Metric ID and output name.
     *
     * @param metricId The ID of the Metric.
     * @param outputName The name of the output to retrieve.
     * @return The [MetricOutput] if found, or null otherwise.
     */
    override fun findByMetricIdAndName(metricId: Int, outputName: String?): MetricOutput? {
        val query =
                "SELECT MetricOutputID, OutputName, Unit, MetricMetricID FROM MetricOutput WHERE MetricMetricID = ? AND OutputName = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        statement.setString(2, outputName)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            MetricOutput(
                    metricOutputId = resultSet.getInt("MetricOutputID"),
                    outputName = resultSet.getString("OutputName"),
                    unit = resultSet.getString("Unit"),
                    metricMetricId = resultSet.getInt("MetricMetricID")
            )
        } else null
    }

    /**
     * Saves a new MetricOutput to the database.
     *
     * @param metricOutput The [MetricOutput] entity to save.
     * @return The generated ID of the inserted MetricOutput.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(metricOutput: MetricOutput): Int {
        val query = "INSERT INTO MetricOutput (OutputName, Unit, MetricMetricID) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, metricOutput.outputName)
        statement.setString(2, metricOutput.unit)
        statement.setInt(3, metricOutput.metricMetricId)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert MetricOutput")
    }

    /**
     * Updates the unit of an existing MetricOutput.
     *
     * @param metricOutput The [MetricOutput] entity with updated information.
     */
    override fun update(metricOutput: MetricOutput) {
        val query = "UPDATE MetricOutput SET Unit = ? WHERE MetricOutputID = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, metricOutput.unit)
        statement.setInt(2, metricOutput.metricOutputId!!)
        statement.executeUpdate()
    }
}