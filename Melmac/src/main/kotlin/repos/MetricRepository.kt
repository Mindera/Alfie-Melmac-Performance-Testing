package repos

import domain.Metric
import java.sql.Connection
import repos.IRepos.IMetricRepository

/**
 * Repository implementation for accessing and managing Metric entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class MetricRepository(private val connection: Connection) : IMetricRepository {

    /**
     * Retrieves all Metric records from the database.
     *
     * @return A list of all [Metric] entities.
     */
    override fun findAll(): List<Metric> {
        val query = "SELECT MetricID, MetricName FROM Metric"
        val statement = connection.prepareStatement(query)
        val resultSet = statement.executeQuery()

        val metrics = mutableListOf<Metric>()
        while (resultSet.next()) {
            metrics.add(
                    Metric(
                            metricId = resultSet.getInt("MetricID"),
                            metricName = resultSet.getString("MetricName")
                    )
            )
        }
        return metrics
    }

    /**
     * Finds a Metric by its unique identifier.
     *
     * @param metricId The ID of the Metric to retrieve.
     * @return The [Metric] if found, or null otherwise.
     */
    override fun findById(metricId: Int): Metric? {
        val query = "SELECT MetricID, MetricName FROM Metric WHERE MetricID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Metric(
                    metricId = resultSet.getInt("MetricID"),
                    metricName = resultSet.getString("MetricName")
            )
        } else null
    }

    /**
     * Finds a Metric by its name.
     *
     * @param name The name of the Metric to retrieve.
     * @return The [Metric] if found, or null otherwise.
     */
    override fun findByName(name: String): Metric? {
        val query = "SELECT MetricID, MetricName FROM Metric WHERE MetricName = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, name)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Metric(
                    metricId = resultSet.getInt("MetricID"),
                    metricName = resultSet.getString("MetricName")
            )
        } else null
    }

    /**
     * Saves a new Metric to the database.
     *
     * @param metric The [Metric] entity to save.
     * @return The generated ID of the inserted Metric.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(metric: Metric): Int {
        val query = "INSERT INTO Metric (MetricName) VALUES (?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, metric.metricName)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert Metric")
    }
}